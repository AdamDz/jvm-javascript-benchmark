package com.company;

import jdk.nashorn.api.scripting.JSObject;

import javax.script.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NashornCallTypesBenchmark {

    static final int RUNS = 30;
    static final int BATCH = 1000;

    static String readFile(String fileName) throws IOException,FileNotFoundException {
        return new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
    }

    static void nashornInvokeMethod(String code) throws ScriptException,NoSuchMethodException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");

        engine.eval(code);
        Invocable inv = (Invocable) engine;
        JSObject star = (JSObject) engine.get("star");

        Object result = null;
        Object property;
        long total = 0;
        for (int i = 0; i < RUNS; ++i) {
            long start = System.nanoTime();
            for (int j = 0; j < BATCH; ++j) {
                property = star.getMember("ssn");
                result = inv.invokeMethod(property, "clean", "12345678");

            }
            long stop = System.nanoTime();
            System.out.println("Run #" + (i + 1) + ": " + Math.round((stop - start)/BATCH/1000) + " us");
            total += (stop - start);
        }
        System.out.println("Average run: " + Math.round(total/RUNS/BATCH/1000) + " us");
        System.out.println("Data is " + ((Invocable) engine).invokeMethod(result, "toString").toString());
    }

    static void nashornCompiledScript(String code) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");

        CompiledScript compiled = ((Compilable) engine).compile(code);

        Object result = null;
        ScriptContext context = new SimpleScriptContext();
        Bindings engineScope = context.getBindings(ScriptContext.ENGINE_SCOPE);
        long total = 0;
        for (int i = 0; i < RUNS; ++i) {
            long start = System.nanoTime();
            for (int j = 0; j < BATCH; ++j) {
                engineScope.put("value", "12345678");
                result = compiled.eval(context);
            }
            long stop = System.nanoTime();
            System.out.println("Run #" + (i + 1) + ": " + Math.round((stop - start)/BATCH/1000) + " us");
            total += (stop - start);
        }
        System.out.println("Average run: " + Math.round(total/RUNS/BATCH/1000) + " us");
        System.out.println("Data is " + ((Invocable) engine).invokeMethod(result, "toString").toString());
    }

    public static void main(String[] args) {
        try {
            String universeCode = readFile("scripts/universe.js");
            System.out.println("== Nashorn invokeMethod ==");
            nashornInvokeMethod(universeCode);
            System.out.println("== Nashorn CompiledScript ==");
            nashornCompiledScript(readFile("scripts/compiledScript.js"));
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

