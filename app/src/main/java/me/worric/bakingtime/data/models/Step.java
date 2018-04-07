
package me.worric.bakingtime.data.models;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Step {

    @SerializedName("description")
    private String mDescription;
    @SerializedName("id")
    private Long mId;
    @SerializedName("shortDescription")
    private String mShortDescription;
    @SerializedName("thumbnailURL")
    private String mThumbnailURL;
    @SerializedName("videoURL")
    private String mVideoURL;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        mShortDescription = shortDescription;
    }

    public String getThumbnailURL() {
        return mThumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        mThumbnailURL = thumbnailURL;
    }

    public String getVideoURL() {
        return mVideoURL;
    }

    public void setVideoURL(String videoURL) {
        mVideoURL = videoURL;
    }

}
