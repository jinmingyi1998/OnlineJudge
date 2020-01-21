package cn.edu.zjnu.learncs.util;

import cn.edu.zjnu.learncs.config.Config;
import cn.edu.zjnu.learncs.config.JudgeSwitchConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
@Component
public class DataManager {

    @Autowired
    Config config;
    @Autowired
    JudgeSwitchConfig judgeSwitchConfig;

    @PostConstruct
    public void init() {
//        if (judgeSwitchConfig.getAutoCreateDir()) {
//            initCreating(config.getRootDir());
//            initCreating(config.getSrcDir());
//            initCreating(config.getDataDir());
//            initCreating(config.getTmpDir());
//            initCreating(config.getOutDir());
//        }
    }

    private void initCreating(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            log.info(filename + " Existed");
        } else {
            if (file.mkdirs())
                log.info(filename + " not existed, created");
            else
                log.error(filename + " not existed, created fail");
        }

    }

    public DataManager() {
    }

    public void unZipFiles(File file, String dir) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration zlist = zipFile.entries();
        while (zlist.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zlist.nextElement();
            String entryName = entry.getName();
            InputStream inputStream = zipFile.getInputStream(entry);
            String outPath = dir + entryName;
            if (new File(outPath).isDirectory()) {
                continue;
            }
            log.info("Unzip File: " + outPath);
            OutputStream outputStream = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = inputStream.read(buf1)) != -1) {
                outputStream.write(buf1, 0, len);
            }
            inputStream.close();
            outputStream.close();
        }
    }

    public void moveFiles() {
    }

    public void deleteFiles(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            for (int i = 0; i < file.list().length; i++) {
                deleteFiles(path + file.list()[i]);
            }
        }
        file.delete();
    }

    public void uploadDataFromZip(File file, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            deleteFiles(destDir);
            dir.mkdirs();
        }
        unZipFiles(file, destDir);
    }
}
