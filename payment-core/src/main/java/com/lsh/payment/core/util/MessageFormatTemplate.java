package com.lsh.payment.core.util;

import javax.annotation.Nullable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class MessageFormatTemplate {

   public static String format(String template, @Nullable Object... args) {
        template = String.valueOf(template); // null -> "null"

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }


    public static void main(String[] args) {
        String desc = "book";
        String index = "90337";
        int size = 4000;
        System.out.println(format("%s (%s) must be less than size (%s)", desc, index, size,"last"));
    }
}
