package cn.edu.zjnu.learncs.util;

import cn.edu.zjnu.learncs.config.Config;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class Compiler extends SandboxApi {
    @Autowired
    private Config config;

    protected Compiler() {
    }

    /**
     * @param filename source file
     * @param solution
     * @param lang     Language Code 0-5 ==>  c,cpp,java,py2,py3
     */
    public void init(String filename, Solution solution, int lang) {
        super.init();
        uid = Integer.parseInt(config.getUid());
        gid = Integer.parseInt(config.getGid());
        max_cpu_time = 4000;
        run_dir = config.getSrcDir() + solution.getId() + "/";
        max_real_time = 6000;
        max_memory = 128 * 1024 * 1024;
        max_stack = 128 * 1024 * 1024;
        switch (lang) {
            case 0:
                exe_path = "/usr/bin/gcc";
                args = filename + " " + config.getCompile().getC().getArgs() + " -o " + run_dir + "main";
                args = args.replaceAll(" ", " --args=");
                break;
            case 1:
                exe_path = "/usr/bin/g++";
                args = filename + " -o " + run_dir + "main " + config.getCompile().getCpp().getArgs();
                args = args.replaceAll(" ", " --args=");
                break;
            case 2:
                exe_path = "/usr/bin/javac";
                args = filename + " -d " + run_dir + " " + config.getCompile().getJava().getArgs();
                args = args.replaceAll(" ", " --args=");
                max_memory = -1;
                break;
            case 3:
                exe_path = "/usr/bin/python";
                args = config.getCompile().getPython2().getArgs() + " " + filename;
                args = args.replaceAll(" ", " --args=");
                break;
            case 4:
                exe_path = "/usr/bin/python3";
                args = config.getCompile().getPython3().getArgs() + " " + filename;
                args = args.replaceAll(" ", " --args=");
                break;
        }
        output_path = run_dir + "compile";//compile output
        log_path = run_dir + "compilelog";//compile log
        script = String.format("%s " +
                        " --max_cpu_time=%d " +
                        " --max_real_time=%d " +
                        " --max_memory=%d" +
                        " --max_stack=%d " +
                        " --max_process_number=%d " +
                        " --max_output_size=%d " +
                        " --exe_path=%s " +
                        "--log_path=%s" +
                        " --output_path=%s " +
                        " --error_path=%s" +
                        " --args=%s " +
                        " --uid=%s " +
                        " --gid=%s " +
                        "--env=PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                config.getJudgerDir(),
                max_cpu_time,
                max_real_time,
                max_memory,
                max_stack,
                max_process_number,
                max_output_size,
                exe_path,
                log_path,
                output_path,
                output_path,
                args,
                uid,
                gid);
    }
}
