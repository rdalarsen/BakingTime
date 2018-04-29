package me.worric.bakingtime.data.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Immutable, parcelable data model class specific to the DetailsFragment
 */
public class StepDetails implements Parcelable {

    public final int stepIndex;

    public final int numSteps;

    public final String stepUrl;

    public final String stepInstructions;

    private StepDetails(int stepIndex, int numSteps, String stepUrl, String stepInstructions) {
        this.stepIndex = stepIndex;
        this.numSteps = numSteps;
        this.stepUrl = stepUrl;
        this.stepInstructions = stepInstructions;
    }

    public static StepDetails newInstance(int stepIndex, int numSteps, String stepUrl, String stepInstructions) {
        return new StepDetails(stepIndex, numSteps, stepUrl, stepInstructions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepDetails that = (StepDetails) o;

        String thisEqualsString = "" + stepIndex + numSteps + stepUrl + stepInstructions;
        String thatEqualsString = "" + that.stepIndex + that.numSteps + that.stepUrl + that.stepInstructions;
        return thisEqualsString.equals(thatEqualsString);
    }

    @Override
    public int hashCode() {
        int result = stepIndex;
        result = 31 * result + (stepUrl != null ? stepUrl.hashCode() : 0);
        result = 31 * result + (stepInstructions != null ? stepInstructions.hashCode() : 0);
        return result;
    }

    // Auto generated Parcelable implementation

    protected StepDetails(Parcel in) {
        stepIndex = in.readInt();
        numSteps = in.readInt();
        stepUrl = in.readString();
        stepInstructions = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stepIndex);
        dest.writeInt(numSteps);
        dest.writeString(stepUrl);
        dest.writeString(stepInstructions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StepDetails> CREATOR = new Creator<StepDetails>() {
        @Override
        public StepDetails createFromParcel(Parcel in) {
            return new StepDetails(in);
        }

        @Override
        public StepDetails[] newArray(int size) {
            return new StepDetails[size];
        }
    };

}
