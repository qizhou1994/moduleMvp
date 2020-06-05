package com.zq.modulemvp.basemvp.base;

import android.Manifest;
import android.util.SparseArray;

/**
 * desc
 * author zhouqi
 * data 2020/6/3
 */
public class Constants {
    private Constants() {
    }

    public final static class APP {
        public static final String DEFAULT_VERSION_NAME = "0.1.0";
        public static final class Route {
            public static final String MAIN = "/app/main";
        }
    }

    public final static class Index {
        public static final class Route {
            public static final String MAIN = "/index/main";
            public static final String ROOT = "/index/root";
            public static final String HOME = "/index/home";
            public static final String MINE = "/index/mine";
        }
    }

    public final static class Account {
        public static final class Route {
            public static final String MAIN = "/account/main";
            public static final String LOGIN = "/account/login";
            public static final String LOGIN_WX = "/account/login/wx";
            public static final String WRAPPER = "/account/wrapper";
            public static final String LOGIN_SMS = "/account/login/sms";
        }

        public static final int TYPE_SMS_PWD_FORGET = 0;
        public static final int TYPE_SMS_PWD_LOGIN = 1;
        public static final int TYPE_SMS_PWD_TRANSACTION = 2;
        public static final int TYPE_SMS_WITHDRAW = 3;
        public static final int TYPE_SMS_NEW_WALLET = 4;

        public static final class LOGIN_CHANNEL {
            public static final String WECHAT = "wechat";
            public static final String FLASH = "flash";
            public static final String PHONE = "phone";
        }
    }

    public final static class Mine {
        public static final class Route {
            public static final String MAIN = "/mine/main";
            public static final String TEAM = "/mine/team";

        }
    }


    public static class Permission {
        public static final SparseArray<String[]> HOLDER = new SparseArray<String[]>();
        public static final int REQ_PERMISSION_START = 1000;

        public static final String[] PHONE = new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
        };

        public static final String[] SMS = new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
        };

        public static final String[] CONTACT = new String[]{
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
        };

        public static final String[] CALENDAR = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
        };

        public static final String[] LOCATION = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        public static final String[] STORAGE = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        public static final String[] CAMERA = new String[]{
                Manifest.permission.CAMERA,
        };

        public static final String[] VIBRATE = new String[]{
                Manifest.permission.VIBRATE,
        };

        static {
            HOLDER.put(REQ_PERMISSION_START, PHONE);
            HOLDER.put(REQ_PERMISSION_START + 1, SMS);
            HOLDER.put(REQ_PERMISSION_START + 2, CONTACT);
            HOLDER.put(REQ_PERMISSION_START + 3, CALENDAR);
            HOLDER.put(REQ_PERMISSION_START + 4, LOCATION);
            HOLDER.put(REQ_PERMISSION_START + 5, STORAGE);
            HOLDER.put(REQ_PERMISSION_START + 6, CAMERA);
            HOLDER.put(REQ_PERMISSION_START + 7, VIBRATE);
        }
    }
}
