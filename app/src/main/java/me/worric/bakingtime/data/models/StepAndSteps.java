package me.worric.bakingtime.data.models;

import android.support.annotation.NonNull;

import java.util.List;

public class StepAndSteps {

    public final Step currentStep;

    public final List<Step> steps;

    private StepAndSteps(Step currentStep, List<Step> steps) {
        this.currentStep = currentStep;
        this.steps = steps;
    }

    public static StepAndSteps newInstance(@NonNull Step step, @NonNull List<Step> steps) {
        return new StepAndSteps(step, steps);
    }

}
