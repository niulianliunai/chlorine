package com.chlorine.base.mvc.util;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ClassGeneratorUtil {
    public static void generate(String entityPath,String outputPath, String packageName, String baseClassPath) {
        if (!FileUtil.exist(entityPath)) {
            throw new RuntimeException("缺少entity");
        }

        generateQTableConstantClass(entityPath,outputPath,packageName);
        generateJpaQueryConfig(outputPath,packageName);

        String repositoryPath = outputPath + "/repository/";
        String servicePath = outputPath + "/service/";
        String controllerPath = outputPath + "/controller/";
        File repositoryDir = new File(repositoryPath);
        File serviceDir = new File(servicePath);
        File controllerDir = new File(controllerPath);
        if (!repositoryDir.exists()) {
            repositoryDir.mkdirs();
        }
        if (!serviceDir.exists()) {
            serviceDir.mkdirs();
        }
        if (!controllerDir.exists()) {
            controllerDir.mkdirs();
        }
        List<String> entityNames = FileUtil.listFileNames(entityPath);
        entityNames.forEach(entityName -> {
            entityName = entityName.replace(".java", "");
            File repositoryFile = new File(repositoryPath + entityName + "Repository.java");
            File serviceFile = new File(servicePath + entityName + "Service.java");
            File controllerFile = new File(controllerPath + entityName + "Controller.java");
            generateFile(repositoryFile, getRepository(packageName, entityName, baseClassPath));
            generateFile(serviceFile, getService(packageName, entityName, baseClassPath));
            generateFile(controllerFile, getController(packageName, entityName, baseClassPath));
        });
    }

    public static void generateFile(File file, String content) {
        if (file.exists()) {
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRepository(String packageName, String entity, String baseClassPath) {
        return getRepositoryTemplate().replace("#{packageName}", packageName).replace("#{entity}", entity).replace("#{baseClassPath}", baseClassPath);
    }

    public static String getService(String packageName, String entity, String baseClassPath) {
        return getServiceTemplate().replace("#{packageName}", packageName).replace("#{entity}", entity).replace("#{baseClassPath}", baseClassPath);
    }

    public static String getController(String packageName, String entity, String baseClassPath) {
        return getControllerTemplate().replace("#{packageName}", packageName).replace("#{entity}", entity).replace("#{baseClassPath}", baseClassPath);
    }

    public static String getRepositoryTemplate() {
        return "package #{packageName}.repository;\n" +
                "\n" +
                "import #{baseClassPath}.repository.BaseRepository;\n" +
                "import #{packageName}.entity.#{entity};\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "\n" +
                "@Repository\n" +
                "public interface #{entity}Repository extends BaseRepository<#{entity}> {}";
    }


    public static String getServiceTemplate() {
        return "package #{packageName}.service;\n" +
                "\n" +
                "import #{baseClassPath}.service.BaseService;\n" +
                "import #{packageName}.entity.#{entity};\n" +
                "import #{packageName}.repository.#{entity}Repository;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "@Service\n" +
                "public class #{entity}Service extends BaseService<#{entity}> {\n" +
                "    protected #{entity}Service(#{entity}Repository #{entity}Repository) {\n" +
                "        super(#{entity}Repository);\n" +
                "    }\n" +
                "}\n";
    }

    public static String getControllerTemplate() {
        return "package #{packageName}.controller;\n" +
                "\n" +
                "import #{baseClassPath}.controller.BaseController;\n" +
                "import #{packageName}.entity.#{entity};\n" +
                "import #{packageName}.service.#{entity}Service;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "\n" +
                "@RestController\n" +
                "@RequestMapping(\"#{entity}\")\n" +
                "public class #{entity}Controller extends BaseController<#{entity}> {\n" +
                "    public #{entity}Controller(#{entity}Service service) {\n" +
                "        super(service);\n" +
                "    }\n" +
                "}\n";
    }

    private static void generateQTableConstantClass(String entityPath,String outputPath, String packageName) {
        File entityFolder = new File(entityPath);
        File[] files = entityFolder.listFiles();
        if (files == null) {
            System.out.println("无法读取entity文件夹");
            return;
        }
        String[] classNames = Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().endsWith(".java"))
                .map(file -> file.getName().substring(0, file.getName().length() - 5)) // 去掉.java扩展名
                .toArray(String[]::new);
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(".constant;\n");
        sb.append("import ").append(packageName).append(".entity.*;\n");
        sb.append("public class QTableConstant {\n\n");

        for (String className : classNames) {
            // 假设每个类都有一个名为ksRule或ksFilter的静态成员
            String fieldName = "q" + className.substring(0, 1).toUpperCase() + className.substring(1);
            String initializer = "Q" + className + "." + className.substring(0, 1).toLowerCase() + className.substring(1); // 或 className + ".ksFilter"，根据需要选择
            sb.append("    public static final Q").append(className).append(" ").append(fieldName).append(" = ").append(initializer).append(";\n\n");
        }

        sb.append("}\n");
        File constantPath = new File(outputPath+"/constant/");
        if (!constantPath.exists()) {
            constantPath.mkdirs();
        }
        generateFile(new File(outputPath + "/constant/QTableConstant.java"), sb.toString());
    }

    private static void generateJpaQueryConfig(String filePath, String packageName) {
        File file = new File(filePath + "/config/JpaQueryConfig.java");
        if (file.exists()) {
            System.out.println("文件已存在");
            return;
        }

        String content = "package " + packageName + ".config;\n" +
                "\n" +
                "import com.querydsl.jpa.impl.JPAQueryFactory;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.stereotype.Component;\n" +
                "\n" +
                "import javax.persistence.EntityManager;\n" +
                "\n" +
                "@Component\n" +
                "public class JpaQueryConfig {\n" +
                "    @Bean\n" +
                "    public JPAQueryFactory jpaQuery(EntityManager entityManager) {\n" +
                "        return new JPAQueryFactory(entityManager);\n" +
                "    }\n" +
                "};";

        File configPath = new File(filePath+"/config/");
        if (!configPath.exists()) {
            configPath.mkdirs();
        }
        generateFile(file, content);
    }
}
