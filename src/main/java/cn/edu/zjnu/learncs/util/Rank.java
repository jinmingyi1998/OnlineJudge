package cn.edu.zjnu.learncs.util;

import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.oj.Contest;
import cn.edu.zjnu.learncs.entity.oj.ContestProblem;
import cn.edu.zjnu.learncs.entity.oj.Problem;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Duration;
import java.util.*;

@Data
public class Rank {
    @JsonIgnore
    private Map<User, RankRow> rowMap = new HashMap<>();
    private Integer problemsNumber = 0;
    @JsonIgnore
    private Map<Long, ContestProblem> cpmap = new HashMap<>();
    @JsonIgnore
    private List<Boolean>problemHasAc;

    public List<RankRow> getRows() {
        List<RankRow> rows = new ArrayList<>(rowMap.values());
        Collections.sort(rows);
        if (rows.size() > 0) {
            int cnt = 0;
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).getUser().getName().contains("*")) {
                    rows.get(i).setOrder(0);
                    continue;
                }
                if (cnt == 0 || rows.get(i).getScore() < rows.get(i - 1).getScore())
                    cnt += 1;
                rows.get(i).setOrder(cnt);
            }
        }
        return rows;
    }

    public Rank() {
    }

    public Rank(Contest c) {
        init(c);
    }

    public Rank init(Contest contest) {
        for (ContestProblem cp : contest.getProblems()) {
            cpmap.put(cp.getProblem().getId(), cp);
        }
        problemsNumber = contest.getProblems().size();
        problemHasAc= new ArrayList<>();
        for (int i = 0; i < problemsNumber; i++) {
            problemHasAc.add(false);
        }
        return this;
    }

    /**
     * the solution should be sorted by time asc
     * @param solution
     * @return rank
     */
    public Rank update(Solution solution) {
        try {
            Solution s = solution.clone();
            Problem p = Problem.jsonReturnProblemFactory();
            ContestProblem cp = cpmap.get(solution.getProblem().getId());
            if (cp == null) {
                return this;
            }
            p.setId(cp.getTempId());
            s.setProblem(p);
            User u = solution.getUser();
            if (rowMap.containsKey(u)) {
                rowMap.get(u).update(s);
            } else {
                RankRow row = new RankRow(problemsNumber, s.getUser());
                row.update(s);
                rowMap.put(s.getUser(), row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Data
    class RankRow implements Comparable {
        @JsonIgnore
        private User user;
        private Long score=0l;
        private ArrayList<RankBox> boxes;
        private Integer order = null;

        public RankRow(int problemNumber, User user) {
            this.user = user;
            boxes = new ArrayList<>();
            for (int i = 0; i < problemNumber; i++) {
                boxes.add(new RankBox(i + 1));
            }
            order = null;
            score = 0l;
        }

        public Integer getOrder() {
            return order == null ? 0 : order;
        }

        public RankRow update(Solution solution) {
            if (solution.getResult().equals(Solution.PENDING)) {
                return this;
            }
            RankBox box = boxes.get(solution.getProblem().getId().intValue() - 1);
            if (box.getAccepted()){
                return this;
            }
            box.update(solution);
            if (solution.getResult().equals(Solution.AC)) {
                score+=box.getTime();
            }
            return this;
        }

        @Override
        public int compareTo(Object o) {
            return getScore().compareTo(((RankRow) o).getScore()) * -1;
        }
    }

    @Data
    class RankBox {
        private Boolean accepted;
        private Long time;
        private Integer submit;
        private Integer pid;
        private Boolean first;
        public RankBox(int pid) {
            this.pid = pid;
            time=0l;
            submit=0;
            accepted=false;
            first=false;
        }

        public RankBox update(Solution s) {
            if (accepted)
                return this;
            submit += 1;
            if (s.getResult().equals(s.AC)) {
                time += Duration.between(s.getContest().getStartTime(), s.getSubmitTime()).toMinutes();
                accepted = true;
                if(!getProblemHasAc().get(pid-1)){
                    first=true;
                    getProblemHasAc().set(pid-1,false);
                }
            } else {
                time += 20;
            }
            return this;
        }
    }

}
