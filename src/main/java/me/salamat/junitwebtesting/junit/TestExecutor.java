package me.salamat.junitwebtesting.junit;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

@AllArgsConstructor
public class TestExecutor {


    private final String className;
    private final String sourceCode;

    static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();
    static final StandardJavaFileManager FILE_MANAGER = JAVA_COMPILER.getStandardFileManager(null, Locale.getDefault(), null);

    static final File COMPILE_DIR;

    static{
        COMPILE_DIR = new File("./classDir");
        COMPILE_DIR.mkdirs();
        try {
            FILE_MANAGER.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(COMPILE_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @SneakyThrows
    public TestResult runTest(){
        

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        //Create Compilation Task with our current JVM Class Path, the File Manager with custom output location, Diagnostics Collector & our SourceCode
        JavaCompiler.CompilationTask task = JAVA_COMPILER.getTask(null,
                FILE_MANAGER, diagnostics,
                Arrays.asList("-classpath", System.getProperty("java.class.path")),
                null, List.of(new JavaSourceFromString(className, sourceCode)));
        
        boolean compiled = task.call();

        if(!compiled) {
            return new TestResult(TestResult.TestResultType.COMPILATION_FAILED, formatDiagnostics(diagnostics));
        }
        //Class Loader for the entire Dir(this would clash if multiple users were active at once)
        URLClassLoader classLoader =  new URLClassLoader(new URL[]{new File("./classDir/").toURI().toURL()});

        final Class<?> jUnitTest = Class.forName(className, true, classLoader);

        //JUnit Test Launcher
        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(new LauncherDiscoveryRequestBuilder().selectors(selectClass(jUnitTest)).build());
        final TestExecutionSummary summary = listener.getSummary();
        cleanUp(classLoader);
        //Setting it to null so next GC will remove it & its loaded classes
        classLoader = null;

        final TestResult.TestResultType type = !summary.getFailures().isEmpty() ? TestResult.TestResultType.TEST_FAILED : TestResult.TestResultType.TEST_SUCCESS;

        final String failureString = buildFailureString(summary);
        return new TestResult(type, """
                Executed %d Tests
                Succeeded: %d
                Failed: %d
                
                Verdict: %s
                Failures: %s
                """.formatted(summary.getTestsStartedCount(), summary.getTestsSucceededCount(), summary.getTestsFailedCount(), type.name(), failureString.isEmpty() ? "NONE" : failureString));
    }

    private static String buildFailureString(TestExecutionSummary summary) {
        StringBuilder failureString = new StringBuilder();
        summary.getFailures().forEach(failure -> failureString.append("Fail at Test with DisplayName: ").append(failure.getTestIdentifier().getDisplayName()).append("\n"));
        return failureString.toString();
    }


    private void cleanUp(URLClassLoader classLoader) {
        try(Stream<Path> paths = Files.list(Path.of(TestExecutor.COMPILE_DIR.toURI()))){
            paths.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            classLoader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String formatDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
        StringBuilder builder = new StringBuilder();
        for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
           builder.append("Error on line %d in %s%n".formatted(
                    diagnostic.getLineNumber(),
                    diagnostic.getSource().toUri()));
        return builder.toString();
    }


}
