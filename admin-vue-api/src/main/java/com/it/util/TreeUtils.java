package com.it.util;

import com.it.pojo.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TreeUtils {

    /**
     * list 转通用树
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T extends TreeNode<T>> List<T> buildTree(List<T> list) {
        List<T> result = new ArrayList<>();

        //通过入参list整合成以节点id做为key,节点作为value Map
        Map<String, T> mapNode = list.stream().collect(Collectors.toMap(T::getId, T -> T));
        for (T node : list) {
            T parent = mapNode.get(node.getParent_id());
            if (parent != null) {
                parent.getChildren().add(node);
                continue;
            }
            result.add(node);
        }
        return result;
    }
}
