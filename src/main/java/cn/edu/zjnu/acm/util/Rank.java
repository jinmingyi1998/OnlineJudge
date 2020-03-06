package cn.edu.zjnu.acm.util;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.ContestProblem;
import cn.edu.zjnu.acm.entity.oj.Problem;
import cn.edu.zjnu.acm.entity.oj.Solution;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;
import java.util.*;

@Data
public class Rank implements Serializable {
    @JsonIgnore
    private Map<User, RankRow> rowMap = new HashMap<>();
    private Integer problemsNumber = 0;
    @JsonIgnore
    private Map<Long, ContestProblem> cpmap = new HashMap<>();
    @JsonIgnore
    private List<Boolean> problemHasAc;

    public Rank() {
    }

    public Rank(Contest c) {
        init(c);
    }

    public List<RankRow> getRows() {
        List<RankRow> rows = new ArrayList<>(rowMap.values());
        Collections.sort(rows);
        if (rows.size() > 0) {
            int cnt = 1;
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).getUser().getName().contains("*")) {
                    rows.get(i).setOrder(-1);
                    continue;
                }
                rows.get(i).setOrder(cnt++);
            }
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).getOrder() == -1) {
                    rows.get(i).setLevel(6);
                } else if (rows.get(i).getOrder() <= Math.ceil(cnt * 0.1)) {
                    rows.get(i).setLevel(0);
                } else if (rows.get(i).getOrder() <= Math.ceil(cnt * 0.3)) {
                    rows.get(i).setLevel(1);
                } else if (rows.get(i).getOrder() <= Math.ceil(cnt * 0.6)) {
                    rows.get(i).setLevel(2);
                } else {
                    rows.get(i).setLevel(3);
                }
            }
        }
        return rows;
    }

    public Rank init(Contest contest) {
        for (ContestProblem cp : contest.getProblems()) {
            cpmap.put(cp.getProblem().getId(), cp);
        }
        problemsNumber = contest.getProblems().size();
        problemHasAc = new ArrayList<>();
        for (int i = 0; i < problemsNumber; i++) {
            problemHasAc.add(false);
        }
        return this;
    }

    /**
     * the solution should be sorted by time asc
     *
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
                rowMap.get(u).update(s, problemHasAc);
            } else {
                RankRow row = new RankRow(problemsNumber, s.getUser());
                row.update(s, problemHasAc);
                rowMap.put(s.getUser(), row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Data
    public static class RankBox implements Serializable {
        private Boolean accepted;
        private Long time;
        private Integer submit;
        private Integer pid;
        private Boolean first;

        public RankBox() {
        }

        public RankBox(int pid) {
            this.pid = pid;
            time = 0l;
            submit = 0;
            accepted = false;
            first = false;
        }

        public RankBox update(Solution s, List<Boolean> problemHasAc) {
            if (accepted)
                return this;
            submit += 1;
            if (s.getResult().equals(Solution.AC)) {
                time = Duration.between(s.getContest().getStartTime(), s.getSubmitTime()).toMinutes();
                accepted = true;
                if (!problemHasAc.get(pid - 1)) {
                    first = true;
                    problemHasAc.set(pid - 1, true);
                }
            } else {
                time += 20;
            }
            return this;
        }
    }

    @Data
    public static class RankRow implements Comparable, Serializable {
        private Integer level;
        private User user;
        private Long penalty = 0L;
        private ArrayList<RankBox> boxes;
        private Integer order = null;
        private Integer solved = 0;

        public RankRow() {
        }

        public RankRow(int problemNumber, User user) throws CloneNotSupportedException {
            this.user = user.clone();
            this.user.setIntro(null);
            this.user.setPassword(null);
            this.user.setEmail(null);
            this.user.setUserProfile(null);
            this.user.setCreatetime(null);
            level = 0;
            boxes = new ArrayList<>();
            for (int i = 0; i < problemNumber; i++) {
                boxes.add(new RankBox(i + 1));
            }
            order = null;
            penalty = 0L;
        }

        public Integer getOrder() {
            return order == null ? 0 : order;
        }

        public RankRow update(Solution solution, List<Boolean> problemHasAc) {
            if (solution.getResult().equals(Solution.PENDING)) {
                return this;
            }
            RankBox box = boxes.get(solution.getProblem().getId().intValue() - 1);
            if (box.getAccepted()) {
                return this;
            }
            box.update(solution, problemHasAc);
            if (solution.getResult().equals(Solution.AC)) {
                penalty += box.getTime();
                penalty += (box.getSubmit() - 1) * 20;
                solved += 1;
            }
            return this;
        }

        @Override
        public int compareTo(Object o) {
            RankRow r = (RankRow) o;
            if (getSolved().compareTo(r.getSolved()) == 0)
                return getPenalty().compareTo(r.getPenalty());
            return getSolved().compareTo(r.getSolved()) * -1;
        }
    }


}
