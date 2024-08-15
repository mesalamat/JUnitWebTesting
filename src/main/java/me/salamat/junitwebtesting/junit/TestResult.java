package me.salamat.junitwebtesting.junit;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TestResult {


    private final TestResultType type;
    private final String message;


    public enum TestResultType{
        COMPILATION_FAILED, TEST_SUCCESS, TEST_FAILED;
    }

}
