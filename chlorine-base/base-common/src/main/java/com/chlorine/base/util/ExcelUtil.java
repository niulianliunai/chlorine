package com.chlorine.base.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExcelUtil {

    public static void exportExcel(JSONObject data, String name, HttpServletResponse response) {
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
        boolean flag = false;
        for (String key : data.keySet()) {
            if (!flag) {
                writer.renameSheet(key);
                flag = true;
            } else {
                writer.setSheet(key);
            }
            writer.write(data.getJSONArray(key), true);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + name + ".xlsx");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);

    }
}
