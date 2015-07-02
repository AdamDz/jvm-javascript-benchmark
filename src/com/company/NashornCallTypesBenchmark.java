package com.company;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

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
        JSObject propertiesDict = (JSObject) engine.get("properties");

        Object result = null;
        Object property;
        long total = 0;
        for (int i = 0; i < RUNS; ++i) {
            long start = System.nanoTime();
            for (int j = 0; j < BATCH; ++j) {
                property = propertiesDict.getMember("ssn");
                result = inv.invokeMethod(property, "clean", "12345678");
            }
            long stop = System.nanoTime();
            System.out.println("Run " + (i*BATCH+1) + "-" + ((i+1)*BATCH) +": " + Math.round((stop - start)/BATCH/1000) + " us");
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
            System.out.println("Run " + (i*BATCH+1) + "-" + ((i+1)*BATCH) +": " + Math.round((stop - start)/BATCH/1000) + " us");
            total += (stop - start);
        }

        System.out.println("Average run: " + Math.round(total/RUNS/BATCH/1000) + " us");
        System.out.println("Data is " + ((Invocable) engine).invokeMethod(result, "toString").toString());
    }

    static void nashornCompiledScriptReturningFunction(String code, String library) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");

        ScriptContext libraryContext = new SimpleScriptContext();
        ScriptContext privateContext = new SimpleScriptContext();

        ScriptObjectMirror errorFunc = (ScriptObjectMirror)((Compilable) engine).compile(library).eval(libraryContext);
        ScriptObjectMirror func = (ScriptObjectMirror)((Compilable) engine).compile(code).eval(privateContext);
        Object result = null;

        long total = 0;
        for (int i = 0; i < RUNS; ++i) {
            long start = System.nanoTime();
            for (int j = 0; j < BATCH; ++j) {
                result = func.call(null, "12345678", errorFunc);
            }
            long stop = System.nanoTime();
            System.out.println("Run " + (i*BATCH+1) + "-" + ((i+1)*BATCH) +": " + Math.round((stop - start)/BATCH/1000) + " us");
            total += (stop - start);
        }

        System.out.println("Average run: " + Math.round(total/RUNS/BATCH/1000) + " us");
        System.out.println("Data is " + result.toString());
    }

    public static void main(String[] args) {
        try {
            String libraryCode = readFile("scripts/library.js");
            String bigScriptCode = readFile("scripts/oneBigScript.js");
            System.out.println("== Nashorn invokeMethod ==");
            nashornInvokeMethod(bigScriptCode);
            System.out.println("== Nashorn CompiledScript ==");
            nashornCompiledScript(readFile("scripts/smallSnippet.js"));
            System.out.println("== Nashorn CompiledScript returning a function ==");
            nashornCompiledScriptReturningFunction(readFile("scripts/functionSnippet.js"), libraryCode);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

