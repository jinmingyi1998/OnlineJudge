package cn.edu.zjnu.acm.util;

import lombok.Data;

/**
 * All directory value end with '/'
 */
@Data
public abstract class SandboxApi {
    protected int max_cpu_time;
    protected int max_real_time;
    protected int max_memory;
    protected int memory_limit_check_only;
    protected int max_stack;
    protected int max_process_number;
    protected int max_output_size;
    protected int uid;
    protected int gid;
    protected String exe_path;
    protected String input_path;
    protected String output_path;
    protected String log_path;
    protected String args;
    protected String run_dir;
    protected String script;
    protected String seccomp;

    protected SandboxApi() {
    }

    public void init() {
        max_cpu_time = 1000;
        max_real_time = max_cpu_time * 2;
        max_memory = 16 * 1024 * 1024;
        memory_limit_check_only = 0;
        max_stack = max_memory + 1024 * 1024;
        max_process_number = 65500;
        max_output_size = 1500 * 1024;
        args = " ";
    }
}
