package z.houbin.xposed.lib.ui.dump;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import z.houbin.xposed.lib.log.Logs;

/**
 * 控件节点信息 dump
 *
 * @author z.houbin
 */
public class UIDump {
    /**
     * 当前UI 所属activity
     */
    private static String activity;

    /**
     * dump 信息
     *
     * @param rootView 根布局
     * @return 节点信息
     */
    public static NodeInfo dump(ViewGroup rootView) {
        activity = null;

        NodeInfo nodeRoot = toNode(rootView);
        if (rootView != null) {
            for (int i = 0; i < rootView.getChildCount(); i++) {
                View childView = rootView.getChildAt(i);
                if (childView instanceof ViewGroup) {
                    NodeInfo childNode = dump((ViewGroup) childView);
                    nodeRoot.addChild(childNode);
                } else {
                    nodeRoot.addChild(toNode(childView));
                }
            }

            if (nodeRoot.isRoot()) {
                if (activity != null) {
                    nodeRoot.setActivity(activity);
                }
            } else if (TextUtils.isEmpty(activity)) {
                Context context = rootView.getContext();
                activity = context.getClass().getCanonicalName();
            }
        }
        return nodeRoot;
    }

    /**
     * 控件转Node 信息
     *
     * @param v 控件
     * @return Node 信息
     */
    private static NodeInfo toNode(View v) {
        NodeInfo nodeInfo = new NodeInfo();
        if (v != null) {
            Context context = v.getContext();
            //包名
            nodeInfo.setPackageName(context.getPackageName());
            //类名
            nodeInfo.setClassName(v.getClass().getCanonicalName());
            //父类名
            Class superClass = v.getClass().getSuperclass();
            if (superClass != null) {
                nodeInfo.setSuperClassName(superClass.getCanonicalName());
            }
            //描述
            CharSequence desc = v.getContentDescription();
            nodeInfo.setDesc(desc == null ? "" : desc.toString());
            //ID
            Resources resources = context.getResources();
            if (View.NO_ID != v.getId()) {
                try {
                    nodeInfo.setId(resources.getResourceEntryName(v.getId()));
                    nodeInfo.setIdInt(v.getId());
                } catch (Resources.NotFoundException e) {
                    //e.printStackTrace();
                }
            }
            //文本
            try {
                Method method = v.getClass().getDeclaredMethod("getText");
                Object r = method.invoke(v);
                if (r != null) {
                    nodeInfo.setText(r + "");
                }
            } catch (Exception e) {
                //Logs.e(e);
            }

            try {
                Method method = v.getClass().getMethod("getText");
                Object r = method.invoke(v);
                if (r != null) {
                    nodeInfo.setText(r + "");
                }
            } catch (Exception e) {
                //
            }
            //可见状态
            nodeInfo.setVisibility(v.getVisibility());
            //位置
            Rect rect = new Rect();
            v.getGlobalVisibleRect(rect);
            nodeInfo.setBounds(rect);
        }
        Logs.e("toNode", nodeInfo.toString());
        return nodeInfo;
    }

    /**
     * Node 信息转换 Json
     *
     * @param nodeRoot Node 信息
     * @return JSON信息
     */
    public static JSONObject nodeToJson(NodeInfo nodeRoot) {
        JSONObject jsonRoot = nodeRoot.dump();
        JSONArray array = new JSONArray();
        for (NodeInfo childNode : nodeRoot.getChildList()) {
            if (childNode.getChildList().isEmpty()) {
                array.put(childNode.dump());
            } else {
                array.put(nodeToJson(childNode));
            }
        }
        try {
            jsonRoot.put("child", array);
        } catch (JSONException e) {
            Logs.e(e);
        }
        return jsonRoot;
    }

    /**
     * dump
     *
     * @param parentGroup 父布局
     * @return dump 信息
     */
    public static String dumpToString(ViewGroup parentGroup) {
        String dumpInfo = "";
        NodeInfo nodeInfo = dump(parentGroup);
        if (nodeInfo != null) {
            JSONObject nodoJson = nodeToJson(nodeInfo);
            if (nodoJson != null) {
                dumpInfo = nodoJson.toString();
            }
        }
        return dumpInfo;
    }
}