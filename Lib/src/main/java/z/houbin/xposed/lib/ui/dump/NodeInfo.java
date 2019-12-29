package z.houbin.xposed.lib.ui.dump;


import android.graphics.Rect;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import z.houbin.xposed.lib.log.Logs;

/**
 * 控件节点信息
 *
 * @author z.houbin
 */
public class NodeInfo {
    /**
     * 资源ID
     */
    private String id;
    /**
     * 资源ID int
     */
    private int idInt;
    /**
     * 类名
     */
    private String className;
    /**
     * 父类名
     */
    private String superClassName;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 文本
     */
    private String text;
    /**
     * 描述
     */
    private String desc;
    /**
     * 界面类
     */
    private String activity;
    /**
     * 可见状态
     */
    private int visibility;
    /**
     * 布局位置
     */
    private Rect bounds;
    /**
     * 深度
     */
    private int deep;
    /**
     * 子节点列表
     */
    private List<NodeInfo> childList = new ArrayList<>();

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getIdInt() {
        return idInt;
    }

    public void setIdInt(int idInt) {
        this.idInt = idInt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<NodeInfo> getChildList() {
        return childList;
    }


    public void addChild(NodeInfo child) {
        this.childList.add(child);
    }

    public boolean isRoot() {
        return getClassName().equals("com.android.internal.policy.DecorView");
    }

    public JSONObject dump() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", (id == null) ? "" : id);
            json.put("idInt", "" + idInt);
            json.put("class", "" + className);
            json.put("superClass", "" + superClassName);
            json.put("text", (text == null) ? "" : text);
            json.put("desc", (desc == null) ? "" : desc);
            json.put("visibility", "" + visibility);
            json.put("deep", "" + deep);
            if (bounds != null) {
                json.put("bounds", String.format(Locale.CHINA, "%d,%d,%d,%d", bounds.left, bounds.top, bounds.right, bounds.bottom));
                json.put("center", bounds.centerX() + "," + bounds.centerY());
            } else {
                json.put("bounds", "");
                json.put("center", "");
            }
            if (activity != null) {
                json.put("activity", "" + activity);
            }
            if (packageName != null) {
                json.put("package", "" + packageName);
            }
        } catch (JSONException e) {
            Logs.e(e);
        }
        return json;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "id='" + id + '\'' +
                ", idInt=" + idInt +
                ", className='" + className + '\'' +
                ", superClassName='" + superClassName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", text='" + text + '\'' +
                ", desc='" + desc + '\'' +
                ", activity='" + activity + '\'' +
                ", visibility=" + visibility +
                ", bounds=" + bounds +
                ", childList=" + childList.size() +
                '}';
    }
}