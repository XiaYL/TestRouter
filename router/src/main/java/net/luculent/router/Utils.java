package net.luculent.router;

import android.os.Bundle;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by xiayanlei on 2017/1/20.
 */
public class Utils {

    public static boolean isParamEmpty(String param) {
        if (TextUtils.isEmpty(param) || TextUtils.isEmpty(param.trim()))
            return true;
        return false;
    }

    public static Integer castInt(String relVal, String defVal) {
        return Integer.valueOf(isParamEmpty(relVal) ? defVal : relVal);
    }

    public static Boolean castBool(String relVal, String defVal) {
        return Boolean.valueOf(isParamEmpty(relVal) ? defVal : relVal);
    }

    public static Long castLong(String relVal, String defVal) {
        return Long.valueOf(isParamEmpty(relVal) ? defVal : relVal);
    }

    public static Double castDouble(String relVal, String defVal) {
        return Double.valueOf(isParamEmpty(relVal) ? defVal : relVal);
    }

    public static Float castFloat(String relVal, String defVal) {
        return Float.valueOf(isParamEmpty(relVal) ? defVal : relVal);
    }

    public static <T> T castValue(Bundle bundle, String key, List<FieldMapping> mappings, Class<T> clazz) {
        if (bundle == null || mappings == null || mappings.size() == 0) {
            return null;
        }
        for (FieldMapping mapping : mappings) {
            if (key.equals(mapping.fieldName)) {
                Object object = null;
                String paramNam = mapping.paramName;
                String initVal = mapping.initValue;
                if (Type.BUNDLE.name().equals(mapping.paramType)) {//传递的为复杂对象，直接从bundle中获取
                    object = bundle.get(paramNam);
                } else {
                    if (Type.STRING.name().equals(mapping.paramType)) {
                        object = bundle.getString(paramNam, initVal);
                    } else if (Type.INTEGER.name().equals(mapping.paramType)) {
                        object = bundle.getInt(paramNam, Integer.valueOf(initVal));
                    } else if (Type.BOOLEAN.name().equals(mapping.paramType)) {
                        object = bundle.getBoolean(paramNam, Boolean.valueOf(initVal));
                    } else if (Type.LONG.name().equals(mapping.paramType)) {
                        object = bundle.getLong(paramNam, Long.valueOf(initVal));
                    } else if (Type.DOUBLE.name().equals(mapping.paramType)) {
                        object = bundle.getDouble(paramNam, Double.valueOf(initVal));
                    } else if (Type.FLOAT.name().equals(mapping.paramType)) {
                        object = bundle.getFloat(paramNam, Float.valueOf(initVal));
                    }
                }
                return castValue(object, clazz);
            }
        }
        return null;
    }

    private static <T> T castValue(Object obj, Class<T> clazz) {
        try {
            return clazz.cast(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
