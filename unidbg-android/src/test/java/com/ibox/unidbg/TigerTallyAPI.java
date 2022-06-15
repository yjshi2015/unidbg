package com.ibox.unidbg;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.LibraryResolver;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TigerTallyAPI extends AbstractJni implements IOResolver<AndroidFileIO> {

    private final AndroidEmulator emulator;
    private final VM vm;
    private final DvmClass cls;

    public TigerTallyAPI() {
        emulator = AndroidEmulatorBuilder
                .for64Bit()
                .setProcessName("iBox")
//                .setProcessName("com.box.art")
                // 导致如下异常！ long com.github.unidbg.arm.backend.dynarmic.Dynarmic.context_alloc(long)
//                .addBackendFactory(new DynarmicFactory(true))
                .build();
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));

//        vm = emulator.createDalvikVM();
        // 创建Android虚拟机,传入APK，Unidbg可以替我们做部分签名校验的工作
        vm = emulator.createDalvikVM(new File("D:\\ibox相关\\0_apk包记录\\v1.1.5\\ibox1.1.5.apk"));
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/resources/example_binaries/arm64-v8a/libtiger_tally.so"), false);
        vm.setJni(this);

        dm.callJNI_OnLoad(emulator);
        cls = vm.resolveClass("com/aliyun/TigerTally/TigerTallyAPI");
        // 初始化函数 _genericNt1
        cls.callStaticJniMethodObject(emulator,
                "_genericNt1(ILjava/lang/String;)I",
                1,
                vm.addLocalObject(new StringObject(vm, "EWA40T3eMNVkLmj8Ur9CuQExbcOti8c3yd-I8xDkLhvphNMuRujkY7V6lKbvAtE2qXa4kTWSnXmo0HXfuUXRgyFNXYwhwvvf7yUYQ-DjWjAa34fjA9yJCam4Llddmcu3D8BQKw4gR-nkYzzOx0uGj9OkfgUHoFxF00akZNyeMrs=")));
    }

    public static void main(String[] args) {
        // 一打印日志就挂起！！！
//        Logger.getLogger("com.github.unidbg.linux.ARM32SyscallHandler").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.unix.UnixSyscallHandler").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.AbstractEmulator").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm.DalvikVM").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm.BaseVM").setLevel(Level.DEBUG);
//        Logger.getLogger("com.github.unidbg.linux.android.dvm").setLevel(Level.DEBUG);
        TigerTallyAPI mainActivity = new TigerTallyAPI();
        String tmp = mainActivity.encrypt("hh");
        System.out.println("=========" + tmp);
    }

    public String encrypt(String reqbody) {
        // ByteArray是Unidbg对字节数组的封装，除此之外还有StringObject等
        ByteArray reqbodyByte = new ByteArray(vm,reqbody.getBytes(StandardCharsets.UTF_8));
        // 初始化函数 _genericNt1
        DvmObject<?> result = cls.callStaticJniMethodObject(emulator,
                "_genericNt3(I[B)Ljava/lang/String;",
                1,
                reqbodyByte);
        return (String) result.getValue();
    }

    // 补环境
    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "com/aliyun/TigerTally/A->ct()Landroid/content/Context;":
                return vm.resolveClass("android/app/Application",vm.resolveClass("android/content/ContextWrapper",vm.resolveClass("android/content/Context"))).newObject(signature);
//            case "com/aliyun/TigerTally/A->pb(Ljava/lang/String;[B)Ljava/lang/String;":
//                return new StringObject(vm,"NaNzfpjiUUl2gNOrCC7S4XS4SD0CH48UatD3GXb5Fh+NYB+0CenYh5nXysYWCfwd+sD4NbdYBDrlKPo5teC09A==");

            // demo 通过vm.resolveClass(xxx).newObject()
//            case "com/izuiyou/common/base/BaseApplication->getAppContext()Landroid/content/Context;":
//                return vm.resolveClass("android/content/Context").newObject(null);
//            case "java/util/UUID->randomUUID()Ljava/util/UUID;":
//                return dvmClass.newObject(UUID.randomUUID());
//            case "android/os/Process->myPid()I":
//                return dvmClass.newObject(emulator.getPid());
//            case "android/os/Debug->isDebuggerConnected()Z":
//                return dvmClass.newObject(false);
        }

        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "android/content/pm/PackageManager->getApplicationInfo(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;":
                return vm.resolveClass("Landroid/content/pm/ApplicationInfo;").newObject(signature);
            case "android/content/pm/PackageManager->getApplicationLabel(Landroid/content/pm/ApplicationInfo;)Ljava/lang/CharSequence;":
                // 直接resolve会报错：class com.xxxxxx.dvm.StringObject
//                return vm.resolveClass("Ljava/lang/CharSequence;");
                return new StringObject(vm,"Ljava/lang/CharSequence;");
            case "android/app/Application->getFilesDir()Ljava/io/File;":
                return vm.resolveClass("Ljava/io/File;");
            case "java/lang/Class->getAbsolutePath()Ljava/lang/String;":
                return new StringObject(vm, "Ljava/lang/String;");
            case "java/lang/String->getAbsolutePath()Ljava/lang/String;":
                return new StringObject(vm, "Ljava/lang/String;");
            case "android/app/Application->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;":
                return vm.resolveClass("Landroid/content/SharedPreferences;");
            // 以下为demo
            // 获取class
//            case "android/content/Context->getClass()Ljava/lang/Class;":
//                return dvmObject.getObjectType();
//            // 获取字符串，需要先hook，获取真实字符串
//            case "java/lang/Class->getSimpleName()Ljava/lang/String;":{
//                return new StringObject(vm, "AppController");
//            }
//            // 连着补，最后直接返回文件路径
//            case "android/content/Context->getFilesDir()Ljava/io/File;":
//            case "java/lang/String->getAbsolutePath()Ljava/lang/String;":
//                return new StringObject(vm, "/data/user/0/cn.xiaochuankeji.tieba/files");
//            // 直接执行对应的方法来获取结果
//            case "java/util/UUID->toString()Ljava/lang/String;":{
//                String uuid = dvmObject.getValue().toString();
//                return new StringObject(vm, uuid);
//            }

        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "android/os/Build->BRAND:Ljava/lang/String;":
                //直接resolveclass报错，class com.xxx.dvm.DvmClass cannot be cast to class com.xxx.dvm.StringObject
//                return vm.resolveClass("Ljava/lang/String;");
                return new StringObject(vm, "Ljava/lang/String;");
            // todo 注意，这个地方可能是硬件异常特征检测
            case "android/os/Build->MODEL:Ljava/lang/String;":
                return new StringObject(vm, "Ljava/lang/String;");
            case "android/os/Build$VERSION->RELEASE:Ljava/lang/String;":
                return new StringObject(vm, "Ljava/lang/String;");
            case "android/os/Build->DEVICE:Ljava/lang/String;":
                return new StringObject(vm, "Ljava/lang/String;");

            // demo
//            case "android/content/Context->TELEPHONY_SERVICE:Ljava/lang/String;":
//                return new StringObject(vm, SystemService.TELEPHONY_SERVICE);
//            case "java/lang/Float->TYPE:Ljava/lang/Class;":
//                return vm.resolveClass("java/lang/Float");
//            case "java/lang/Double->TYPE:Ljava/lang/Class;":
//                return vm.resolveClass("java/lang/Double");
        }
        return super.getStaticObjectField(vm, dvmClass, signature);
    }
    public void destroy() {
        try {
            emulator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public FileResult<AndroidFileIO> resolve(Emulator<AndroidFileIO> emulator, String pathname, int oflags) {
        return null;
    }
}
