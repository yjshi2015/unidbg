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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TigerTallyAPI2 extends AbstractJni implements IOResolver<AndroidFileIO> {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    TigerTallyAPI2() {
        emulator = AndroidEmulatorBuilder.for64Bit().build(); // 创建模拟器实例，要模拟32位或者64位，在这里区分
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("D:\\ibox相关\\0_apk包记录\\v1.1.5\\ibox1.1.5.apk"));
        new AndroidModule(emulator, vm).register(memory);
        vm.setVerbose(false);
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/resources/example_binaries/arm64-v8a/libtiger_tally.so"), true);
        module = dm.getModule();
        vm.setJni(this);
        dm.callJNI_OnLoad(emulator);
        DvmClass dvmClass = vm.resolveClass("com/aliyun/TigerTally/TigerTallyAPI");
        dvmClass.callStaticJniMethodObject(emulator,"_genericNt1(ILjava/lang/String;)I",1,vm.addLocalObject(new StringObject(vm, "EWA40T3eMNVkLmj8Ur9CuQExbcOti8c3yd-I8xDkLhvphNMuRujkY7V6lKbvAtE2qXa4kTWSnXmo0HXfuUXRgyFNXYwhwvvf7yUYQ-DjWjAa34fjA9yJCam4Llddmcu3D8BQKw4gR-nkYzzOx0uGj9OkfgUHoFxF00akZNyeMrs=")));
        DvmObject<?> dvmObject = dvmClass.callStaticJniMethodObject(emulator,"_genericNt3(I[B)Ljava/lang/String;",2,new ByteArray(vm,"".getBytes(StandardCharsets.UTF_8)));
        System.out.println(dvmObject);
    }
    public static void main(String[] args) {
        TigerTallyAPI2 test = new TigerTallyAPI2();

    }


    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "com/aliyun/TigerTally/A->ct()Landroid/content/Context;":
                return vm.resolveClass("android/app/Application",vm.resolveClass("android/content/ContextWrapper",vm.resolveClass("android/content/Context"))).newObject(signature);
            case "com/aliyun/TigerTally/A->pb(Ljava/lang/String;[B)Ljava/lang/String;":
                return new StringObject(vm,"NaNzfpjiUUl2gNOrCC7S4XS4SD0CH48UatD3GXb5Fh+NYB+0CenYh5nXysYWCfwd+sD4NbdYBDrlKPo5teC09A==");
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "android/content/pm/PackageManager->getApplicationInfo(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;":
                return vm.resolveClass("Landroid/content/pm/ApplicationInfo;").newObject(signature);
            case "android/content/pm/PackageManager->getApplicationLabel(Landroid/content/pm/ApplicationInfo;)Ljava/lang/CharSequence;":
                return new StringObject(vm,"Ljava/lang/CharSequence;");
            case "android/app/Application->getFilesDir()Ljava/io/File;":
                return vm.resolveClass("Ljava/io/File;");
            case "java/lang/String->getAbsolutePath()Ljava/lang/String;":
                return new StringObject(vm,"Ljava/lang/String;");
            case "android/app/Application->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;":
                return vm.resolveClass("Landroid/content/SharedPreferences;");
            case "java/lang/Class->getAbsolutePath()Ljava/lang/String;":
                return new StringObject(vm,"Ljava/lang/String;");
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }
    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature){
            case "android/os/Build->BRAND:Ljava/lang/String;":
                return new StringObject(vm,"Ljava/lang/String;");
            case "android/os/Build->MODEL:Ljava/lang/String;":
                return new StringObject(vm,"Ljava/lang/String;");
            case "android/os/Build$VERSION->RELEASE:Ljava/lang/String;":
                return new StringObject(vm,"Ljava/lang/String;");
            case "android/os/Build->DEVICE:Ljava/lang/String;":
                return new StringObject(vm,"Ljava/lang/String;");
        }
        return super.getStaticObjectField(vm,dvmClass,signature);
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
