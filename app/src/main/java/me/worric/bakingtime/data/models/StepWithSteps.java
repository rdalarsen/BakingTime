package me.worric.bakingtime.data.models;

import android.support.annotation.NonNull;

import java.util.List;

public class StepWithSteps {

    public final Step currentStep;

    public final List<Step> currentRecipeSteps;

    private StepWithSteps(Step currentStep, List<Step> currentRecipeSteps) {
        this.currentStep = currentStep;
        this.currentRecipeSteps = currentRecipeSteps;
    }

    public static StepWithSteps newInstance(@NonNull Step step, @NonNull List<Step> steps) {
        return new StepWithSteps(step, steps);
    }

    public int getIndexOfCurrentStep() {
        return currentRecipeSteps.indexOf(currentStep);
    }

}
