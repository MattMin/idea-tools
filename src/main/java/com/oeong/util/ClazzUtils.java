//package com.oeong.util;
//
//import java.io.File;
//import java.net.URL;
//import java.util.*;
//
//public class ClazzUtils {
//
//
//    /**
//     * 获取一个接口的所有实现类
//     * @param target
//     */
//    public static ArrayList<Class<?>> getInterfaceImpls(Class<?> target) throws ClassNotFoundException {
//        ArrayList<Class<?>> subclasses = new ArrayList<>();
//        // 判断class对象是否是一个接口
//        if (target.isInterface()) {
////            Package[] basePackage = Thread.currentThread().getContextClassLoader().getDefinedPackages();
//            URL uri = ClazzUtils.class.getResource("/toolWindow/Calendar-icon.png");
//            File[] files = new File(String.valueOf(uri)).listFiles();
//            if(files==null){
//                return subclasses;
//            }
//            // 存放class路径的list
//            ArrayList<String> classpaths = new ArrayList<>();
//            for (File file : files) {
//                // 扫描项目编译后的所有类
//                if (file.isDirectory()) {
//                    listPackages(file.getName(), classpaths);
//                }
//            }
//            // 获取所有类,然后判断是否是 target 接口的实现类
//            for (String classpath : classpaths) {
//                Class<?> classObject = Class.forName(classpath);
//                if (classObject.getSuperclass() == null) continue; // 判断该对象的父类是否为null
//                Set<Class<?>> interfaces = new HashSet<>(Arrays.asList(classObject.getInterfaces()));
//                if (interfaces.contains(target)) {
//                    subclasses.add(Class.forName(classObject.getName()));
//                }
//            }
//        }
//        return subclasses;
//    }
//
//    /**
//     * 获取项目编译后的所有的.class的字节码文件
//     * 这么做的目的是为了让 Class.forName() 可以加载类
//     *
//     * @param basePackage 默认包名
//     * @param classes     存放字节码文件路径的集合
//     * @return
//     */
//    public static void listPackages(String basePackage, List<String> classes) {
//        URL url = ClazzUtils.class.getClassLoader()
//                .getResource("./" + basePackage.replaceAll("\\.", "/"));
//        File directory = new File(url.getFile());
//        for (File file : directory.listFiles()) {
//            // 如果是一个目录就继续往下读取(递归调用)
//            if (file.isDirectory()) {
//                listPackages(basePackage + "." + file.getName(), classes);
//            } else {
//                // 如果不是一个目录,判断是不是以.class结尾的文件,如果不是则不作处理
//                String classpath = file.getName();
//                if (".class".equals(classpath.substring(classpath.length() - ".class".length()))) {
//                    classes.add(basePackage + "." + classpath.replaceAll(".class", ""));
//                }
//            }
//        }
//    }
//
//
//
//}
