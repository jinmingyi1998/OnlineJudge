package cn.edu.zjnu.learncs.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 递归过滤对象中的指定属性，过滤后的对象与原始对象可能并不完全相等，
 */
public class ObjectFilter {
    //错误栈
    private Stack<HashMap<String, Object>> errorStack;
    //缓存
    private ArrayList<Object> cacheList;

    public ObjectFilter() {
        errorStack = new Stack<>();
        cacheList = new ArrayList<>();
    }

    public Stack<HashMap<String, Object>> getErrorStack() {
        return errorStack;
    }

    /**
     * 判断对象是否在当前的缓存中
     */
    private boolean inCache(Object object) {
        for (Object cache : cacheList) {
            if (cache == object) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将对象获取属性的错误添加到错误栈中
     */
    private void setErrorStack(String key, Class cla, String prefixFilter, String suffixFilter, Exception e) {
        HashMap<String, Object> map = new HashMap();
        map.put("prefixFilter", prefixFilter);
        map.put("suffixFilter", suffixFilter);
        map.put("key", key);
        map.put("class", cla);
        map.put("exception", e);
        errorStack.push(map);
    }

    /**
     * 判断是否为要忽略处理的类型
     */
    private boolean isIgnoreType(Object object) {
        if (object instanceof Integer
                || object instanceof Byte
                || object instanceof Long
                || object instanceof Double
                || object instanceof Float
                || object instanceof Character
                || object instanceof Short
                || object instanceof Boolean
                || object instanceof String
                || object instanceof StringBuilder
                || object instanceof StringBuffer
                || object instanceof Date) {
            return true;
        }
        return false;
    }

    /**
     * 分割过滤词，0是当前过滤词，1是剩下的过滤词，没有则是null
     */
    private String[] subFilter(String filter) {
        //将filter过滤字符串分割成两部分，a.b.c分割成a和b.c
        int i = filter.indexOf(".");
        if (i == -1) {
            return new String[]{filter, null};
        }
        String prefix = filter.substring(0, i);
        String suffix = filter.substring(i + 1);
        return new String[]{prefix, suffix};
    }

    /**
     * 返回一个新的HashMap并过滤Map映射中的指定关键字和属性，对原始Map没有影响
     */
    public Object filterAttributeByMap(Map map, String filter) throws Exception {
        String[] filters = subFilter(filter);   //第一个值是当前要处理的过滤词，第二个值是后续的过滤词
        HashMap<Object, Object> res = new HashMap<>();   //创建新的容器用来保存过滤后的值
        for (Object key : map.keySet()) {
            if (filters[1] == null && key.equals(filters[0])) {  //后续的过滤词已经没有了，处理过滤词
                continue;    //跳过当前属性
            }
            Object value = map.get(key);    //获得对应的值
            value = filterJudgement(filters, key, value);
            res.put(key, value);    //保存当前属性和值
        }
        return res;
    }

    /**
     * 根据提供的过滤词判断是否需要进行递归过滤
     */
    private Object filterJudgement(String[] filters, Object key, Object value) throws Exception {
        //判断是否命中当前过滤词，且不是忽略类型，准备进行递归过滤
        if (value != null && key.equals(filters[0]) && !isIgnoreType(value)) {
            if (value instanceof Map) {   //如果对象是个map映射
                value = filterAttributeByMap((Map) value, filters[1]);  //进行递归过滤
            } else if (value instanceof List) {    //如果对象是个List将遍历所有元素进行过滤
                List list = (List) value;
                if (inCache(list)) {  //如果这个list在缓存中，则直接遍历，不产生新的副本
//                    System.out.println("命中缓存");
                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, filterJudgement(filters, key, list.get(i)));    //直接保存结果
                    }
                    value = list;   //赋值给value准备返回
                } else {
//                    System.out.println("创建缓存");
                    ArrayList temp = null;
                    temp = new ArrayList<>();//创建一个list用来存储新值
                    for (Object item : list) {
                        temp.add(filterJudgement(filters, key, item));    //存入处理后的值
                    }
                    cacheList.add(temp);//将新list保存在缓存中
                    value = temp;   //赋值给value准备返回
                }
            } else if (value instanceof Set) {   //如果是个set，进行遍历操作，set不需要缓存
                Set set = (Set) value;
                HashSet temp = new HashSet();//创建一个set用来存储新值
                for (Object item : set) {
                    temp.add(filterJudgement(filters, key, item));    //存入处理后的值
                }
                cacheList.add(temp);//将新list保存在缓存中
                value = temp;   //赋值给value准备返回
            } else if (value.getClass().isArray()) {   //如果是个数组，进行遍历
                Object[] arr = (Object[]) value;
                if (inCache(arr)) {  //如果这个arr在缓存中，则直接遍历，不产生新的副本
//                    System.out.println("命中缓存");
                    for (int i = 0; i < arr.length; i++) {
                        arr[i] = filterJudgement(filters, key, arr[i]);  //保存处理后的新值
                    }
                    value = arr;//赋值给value准备返回
                } else {
//                    System.out.println("创建缓存");
                    Object[] temp = new Object[arr.length];    //创建一个指定长度的数组用来存储新值
                    for (int i = 0; i < arr.length; i++) {
                        temp[i] = filterJudgement(filters, key, arr[i]);  //保存处理后的新值
                    }
                    cacheList.add(temp);//将新list保存在缓存中
                    value = temp;//赋值给value准备返回
                }
            } else {
                value = filterAttributeByObject(value, filters[1]);//其它类型使用object过滤
            }
        }
        return value;
    }

    /**
     * 将object的属性和值保持在HashMap中，并过滤指定属性，对原始object没有影响
     */
    public Object filterAttributeByObject(Object object, String filter) throws Exception {
        String[] filters = subFilter(filter);   //第一个值是当前要处理的过滤词，第二个值是后续的过滤词
        HashMap<String, Object> res = new HashMap<>();   //创建新的容器用来保存过滤后的值
        Class clazz = object.getClass();    //获得被过滤的对象的类型
        if (clazz.getTypeName().contains("$")) {  //通过类名判断是否为代理对象
            Type genericSuperclass = clazz.getGenericSuperclass();
//            System.out.println("代理对象名称" + clazz.getTypeName());
//            System.out.println("被代理对象名称" + genericSuperclass.getTypeName());
            clazz = Class.forName(genericSuperclass.getTypeName()); //获得被代理对象的class
        }
        Field[] fields = clazz.getDeclaredFields(); //获得被过滤对象的所有成员变量
        for (Field field : fields) {
            String key = field.getName();   //获得属性名称
            if (filters[1] == null && key.equals(filters[0])) {  //后续的过滤词已经没有了，处理过滤词
                continue;    //跳过当前属性
            }
            Object value = null;
            try {
                //准备根据属性名获得属性值
                PropertyDescriptor descriptor = new PropertyDescriptor(key, clazz);
                Method method = descriptor.getReadMethod();
                value = method.invoke(object);   //获得属性值
            } catch (Exception e) {
                setErrorStack(key, clazz, filters[0], filters[1], e); //将获取属性时产生的错误保存在栈中，并继续下一个属性的获取
                continue;
            }
            value = filterJudgement(filters, key, value);   //对value值进行过滤判断，有需要会进行递归过滤
            res.put(key, value);    //保存当前属性和值
        }
        return res;
    }

    /**
     * 将指定对象(Object Map List Array)的属性和值保持在HashMap中，并过滤指定属性，对原始object没有影响
     */
    public Object filterAttribute(Object object, String filters) throws Exception {
        if (filters == null) {
            return object;
        }
        for (String f : filters.split(",")) {
            if ("".equals(f)) {
                continue;
            }
            String[] fArray = new String[]{"", f};
            object = filterJudgement(fArray, "", object);
        }
        return object;
    }
}