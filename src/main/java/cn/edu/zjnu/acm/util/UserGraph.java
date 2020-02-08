package cn.edu.zjnu.acm.util;

import lombok.Data;

@Data
public class UserGraph {
    private PieGraph pie;
    private RadarGraph radar;

    public UserGraph() {
        pie = new PieGraph();
        radar = new RadarGraph();
    }

    @Data
    public class PieGraph {
        private Integer prime;
        private Integer medium;
        private Integer advance;

        public void setPrime(Integer prime) {
            this.prime = prime == null ? 0 : prime;
        }

        public void setMedium(Integer medium) {
            this.medium = medium == null ? 0 : medium;
        }

        public void setAdvance(Integer advance) {
            this.advance = advance == null ? 0 : advance;
        }
    }

    @Data
    public class RadarGraph {
        private Integer data_structure;
        private Integer string;
        private Integer probability;
        private Integer search;
        private Integer graph_theory;
        private Integer dynamic_programming;
        private Integer geometry;
        private Integer math;

        public void setData_structure(Integer data_structure) {
            this.data_structure = data_structure == null ? 0 : data_structure;
        }

        public void setString(Integer string) {
            this.string = string == null ? 0 : string;
        }

        public void setProbability(Integer probability) {
            this.probability = probability == null ? 0 : probability;
        }

        public void setSearch(Integer search) {
            this.search = search == null ? 0 : search;
        }

        public void setGraph_theory(Integer graph_theory) {
            this.graph_theory = graph_theory == null ? 0 : graph_theory;
        }

        public void setDynamic_programming(Integer dynamic_programming) {
            this.dynamic_programming = dynamic_programming == null ? 0 : dynamic_programming;
        }

        public void setGeometry(Integer geometry) {
            this.geometry = geometry == null ? 0 : geometry;
        }

        public void setMath(Integer math) {
            this.math = math == null ? 0 : math;
        }

        public void init() {
            int sum = 1 + data_structure + string + probability + search + graph_theory + dynamic_programming +
                    geometry + math;
            data_structure = (int) (100.0 * data_structure / sum);
            string = ((int) (100.0 * string / sum));
            probability = ((int) (100.0 * probability / sum));
            search = ((int) (100.0 * search / sum));
            graph_theory = ((int) (100.0 * graph_theory / sum));
            dynamic_programming = ((int) (100.0 * dynamic_programming / sum));
            geometry = ((int) (100.0 * geometry / sum));
            math = ((int) (100.0 * math / sum));
        }
    }
}
