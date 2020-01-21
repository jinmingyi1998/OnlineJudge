package cn.edu.zjnu.learncs.util;


import cn.edu.zjnu.learncs.config.Config;
import cn.edu.zjnu.learncs.entity.oj.Problem;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class JudgeQueue {
    @Autowired
    private Config config;
    //    @Autowired
//    private ProblemService problemService;
    @Autowired
    private Judger judger;
    @Autowired
    private Compiler compiler;
//    @Autowired
//    private SolutionService solutionService;
//    @Autowired
//    private CompileErrorRepository compileErrorRepository;


    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        final ThreadGroup group = new ThreadGroup("judgegroup");
        final AtomicInteger counter = new AtomicInteger();
        final ThreadFactory threadFactory = runnable -> new Thread(group, runnable, "judge thread " + counter.incrementAndGet());
        int nThreads = Runtime.getRuntime().availableProcessors();
        executorService = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), threadFactory) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                if (t != null) {
                    log.error("{}", r, t);
                }
            }
        };
    }

    @PreDestroy
    public void destory() {
        executorService.shutdownNow();
    }


    public void solve(Solution solution) {
        CompletableFuture.runAsync(() -> {
            //log.info(solution.toString());
            exec(solution);
        }, executorService);
    }

    private String generateSourceCode(Solution solution, int lang) {
        String postfix;
        switch (lang) {
            case 0:
                postfix = "main.c";
                break;
            case 1:
                postfix = "main.cpp";
                break;
            case 2:
                postfix = "Main.java";
                break;
            case 3:
            case 4:
            default:
                postfix = "main.py";
                break;
        }
        String filename = config.getSrcDir() + solution.getId() + "/" + postfix;
        File file = new File(config.getSrcDir() + solution.getId());
        file.mkdir();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            try {
                fileOutputStream.write(solution.getSource().getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private int compile(String filename, int lang, Solution solution) {
        solution.setResult("Compiling");
//        solutionService.updateSolution(solution);
        compiler.init(filename, solution, lang);
        String script = compiler.getScript();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(script);
            //log.info("Compiling:"+script);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            process.waitFor(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BufferedReader bf = null;
        String output = "";
        try {
            bf = new BufferedReader(new InputStreamReader(new FileInputStream(compiler.run_dir + "compile")));
            String line = "";
            while ((line = bf.readLine()) != null) {
                output += line + "\n";
            }
            bf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Compile output:" + output);
        if (!output.equals("")) {
            //CE
//            CompileError ce = new CompileError(solution, output);
//            compileErrorRepository.save(ce);
//            solution.setCe(ce);
            solution.setResult("Compile Error");
//            solutionService.updateSolution(solution);
            log.info("SOLUTION:" + solution.toString());
//            log.info("CE:" + ce.toString());
            return 1;
        }

        return 0;
    }

    /**
     * @param filename
     * @param lang
     * @param problem
     * @param solution
     * @param case_name
     * @return Accepted = 0
     * CPU_TIME_LIMIT_EXCEEDED = 1
     * REAL_TIME_LIMIT_EXCEEDED = 2
     * MEMORY_LIMIT_EXCEEDED = 3
     * RUNTIME_ERROR = 4
     * SYSTEM_ERROR = 5
     * Wrong = -1
     */
    private int judge(String filename, int lang, Problem problem, Solution solution, String case_name) {
        solution.setResult("Running");
//        solutionService.updateSolution(solution);
        judger.init(filename, problem, solution, lang, case_name);
        String script = judger.getScript();
        script += " > " + judger.getRun_dir() + "output.json & echo $! ";
        try {
            FileOutputStream fo = new FileOutputStream(judger.getRun_dir() + "run.sh");
            fo.write(script.getBytes());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //run the source code
        Process process;
        try {
            process = Runtime.getRuntime().exec("chmod 777 " + judger.getRun_dir() + "run.sh");
            process.waitFor();
            process = Runtime.getRuntime().exec(judger.getRun_dir() + "run.sh");
            BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String pid = bf.readLine();
            bf.close();
            File file = new File(judger.getRun_dir() + "output.json");
            // wait for sandbox finish
            // if finished, there should be context in output.json
            // timeout 30s
            int TLE_flag = 1;
            for (int i = 0; i < 30; i++) {
                Thread.sleep(1000);
                if (file.exists()) {
                    if (new BufferedReader(new InputStreamReader(new FileInputStream(file))).readLine() != null) {
                        TLE_flag = 0;
                        break;
                    }
                }
            }
            if (TLE_flag == 1) {
                log.error("Judger Process Time Out:" + pid);
                process = Runtime.getRuntime().exec("kill " + pid);
                process.destroy();
                return 1;
            }
            //log.info("{}", pid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //get result from json
        try {
            //log.info(judger.getRun_dir() + "output.json");
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(judger.getRun_dir() + "output.json")));
            String json = "";
            String line = "";
            while ((line = bf.readLine()) != null) {
                json += line + "\n";
            }
            log.info("json is :" + json);// TODO json 转换失败
            JSONObject jsonObject = JSON.parseObject(json);
            int exit_code = jsonObject.getInteger("exit_code");
            if (exit_code != 0) {
                return 4;
            }
            int ret = jsonObject.getInteger("result");
            solution.setTime(Math.max(jsonObject.getInteger("real_time"), solution.getTime()));
            solution.setMemory(Math.max(jsonObject.getInteger("memory"), solution.getMemory()));
            if (ret != 0) {
                return ret;
            }
            if (fileCompare(config.getDataDir() + problem.getId() + "/" + case_name + ".out", judger.getRun_dir() + "/" + case_name + ".out")) {
                return 0;
            } else return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2333;
    }

    /**
     * a util method for file comparision
     *
     * @param std
     * @param usr
     * @return true if ac
     * false if wrong
     */
    private boolean fileCompare(String std, String usr) {
        try {
            BufferedReader stdbf = new BufferedReader(new InputStreamReader(new FileInputStream(std)));
            BufferedReader usrbf = new BufferedReader(new InputStreamReader(new FileInputStream(usr)));
            while (true) {
                String linea = stdbf.readLine();
                String lineb = usrbf.readLine();
                if (linea == null && lineb == null) {
                    return true;
                } else if (!Objects.equals(linea, lineb)) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void exec(Solution solution) {
        /**
         * C = 0
         * C++ = 1
         * Java = 2
         * Python2 = 3
         * Python3 = 4
         */
        int lang;
        Problem problem = solution.getProblem();
        switch (solution.getLanguage()) {
            case "c":
                lang = 0;
                break;
            case "cpp":
                lang = 1;
                break;
            case "java":
                lang = 2;
                break;
            case "py2":
                lang = 3;
                break;
            case "py3":
                lang = 4;
                break;
            default:
                lang = 1;
                break;
        }
        String filename = generateSourceCode(solution, lang);
        if (compile(filename, lang, solution) == 0) {
            String case_name = "";
            solution.setResult("Running");
//            solutionService.updateSolution(solution);
            File file = new File(config.getDataDir() + problem.getId());
            if (!file.exists()) {
                log.error("Data directory not Found!");
                return;
            }
            int case_counter = 0;
            for (String f : file.list()) {
                f = f.trim();
                //log.info("data file:" + f);
                if (f.indexOf(".in") != -1) {
                    case_name = f.substring(0, f.length() - 3);
                    case_counter++;
                    solution.setCaseNumber(case_counter);
                    int ret = judge(filename, lang, problem, solution, case_name);
                    switch (ret) {
                        case -1:
                            solution.setResult("Wrong Answer");
                            break;
                        case 0:
                            solution.setResult("Accepted");
                            break;
                        case 1:
                        case 2:
                            solution.setResult("Time Limit Exceed");
                            break;
                        case 3:
                            solution.setResult("Memory Limit Exceed");
                            break;
                        case 4:
                            solution.setResult("Runtime Error");
                            break;
                        case 5:
                            solution.setResult("System Error");
                            break;
                        default:
                            solution.setResult("System Error");
                            break;
                    }
                    if (ret != 0) {
                        break;
                    }
                } else {
                    continue;
                }
            }
            if (case_counter == 0) {
                solution.setResult("System Error");
                log.error("Data no file !");
            }
        } else {
            solution.setResult("Compile Error");
        }
        if (solution.getResult().equals("Accepted")) {
//            solutionService.addAccepted(solution);
        }
//        solutionService.updateSolution(solution);
    }
}
