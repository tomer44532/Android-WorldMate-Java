package com.example.androidsecondproject.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.androidsecondproject.model.Chat;
import com.example.androidsecondproject.model.Match;
import com.example.androidsecondproject.model.NotificationManager;
import com.example.androidsecondproject.model.Profile;
import com.example.androidsecondproject.repository.Repository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SwipeViewModel extends AndroidViewModel {

    private Context context;
    private Repository mRepository;
    private Profile mProfile;
 //   private boolean isFirstTime=true;
    MutableLiveData<List<Profile>> mProfilesMutableLiveData;
    MutableLiveData<List<Profile>> mGuestProfilesMutableLiveData;
    private List<Profile> mProfiles;
    private  List<String> categories;
    private boolean mIsLoginAsGuest;

    public SwipeViewModel(@NonNull Application application) {
        super(application);
        mRepository=Repository.getInstance(application.getApplicationContext());
        categories = new ArrayList<>();
    }
    public MutableLiveData<List<Profile>> getProfilesResultSuccess(){
        if (mProfilesMutableLiveData == null) {
            mProfilesMutableLiveData = new MutableLiveData<>();
            mProfiles=new ArrayList<>();
            loadProfilesData();
        }
        return mProfilesMutableLiveData;
    }
    public MutableLiveData<List<Profile>> getGuestProfilesResultSuccess(){
        if (mGuestProfilesMutableLiveData == null) {
            mGuestProfilesMutableLiveData = new MutableLiveData<>();
            loadGuestProfilesData();
        }
        return mGuestProfilesMutableLiveData;
    }

    private void loadGuestProfilesData() {
        mRepository.setProfileGuestListener(new Repository.ProfilesForGuestListener() {
            @Override
            public void onGuestProfilesChangedSuccess(List<Profile> guestProfiles) {
                mGuestProfilesMutableLiveData.setValue(guestProfiles);
            }
        });
    }

    public void readProfiles(){
        mRepository.readProfiles(mProfile);
    }
    public void setUserProfile(Profile profile){
        this.mProfile=profile;
    }

    private void loadProfilesData() {
        mRepository.setProfilesListener(new Repository.ProfilesListener() {
            @Override
            public void onProfilesDataChangeSuccess(List<Profile> profiles) {
                mProfiles.clear();
                mProfiles.addAll(profiles);
                mProfilesMutableLiveData.setValue(profiles);
            }

            @Override
            public void onProfilesDataChangeFail(String error) {

            }
        });
    }

    public void addLikedProfile(int position) {

        List<String> likes=mProfiles.get(position).getLikes();
        likes.add(mProfile.getUid());
        mRepository.updateProfile(mProfiles.get(position).getUid(),"likes",likes);

    }

    public void addDislikedProfile(int position){
        //  mProfile.getDisLikes().add((mProfilesMutableLiveData.getValue().get(position).getUid()));
        mProfiles.get(position).getDisLikes().add((mProfile.getUid()));
            mRepository.updateProfile(mProfiles.get(position).getUid(),"disLikes",mProfiles.get(position).getDisLikes());
    }


    public boolean checkIfMatch(int position) {
        String otherUid=mProfiles.get(position).getUid();
        // List<String> likeUids=mProfilesMutableLiveData.getValue().get(position).getLikes();

        List<String> likeUids=mProfile.getLikes();
        return likeUids.contains(otherUid);
    }

    public void updateMatch(int position) {
    //    Profile otherPofile=mProfilesMutableLiveData.getValue().get(position);
        Profile otherPofile=mProfiles.get(position);
        String chatKeyId,myUid=mProfile.getUid(),otherUid=otherPofile.getUid();
        if(myUid.compareTo(otherUid)>0){
            chatKeyId=myUid+otherUid;
        }
        else{
            chatKeyId=otherUid+myUid;
        }
        Match myMatch = new Match(otherPofile.getUid(),chatKeyId);
        Match otherMatch = new Match(mProfile.getUid(),chatKeyId);
        final String MATCHES= "matches";

        mProfile.getMatches().add(myMatch);
        mRepository.updateProfile(mProfile.getUid(),MATCHES,mProfile.getMatches());

        otherPofile.getMatches().add(otherMatch);
        mRepository.updateProfile(otherPofile.getUid(),MATCHES,otherPofile.getMatches());

        Chat chat =new Chat(chatKeyId,mProfile.getUid(),otherPofile.getUid());
        mRepository.writeChat(chat);

        mProfile.getLikes().remove(otherPofile.getUid());
        otherPofile.getLikes().remove(mProfile.getUid());
        mRepository.updateProfile(mProfile.getUid(),"likes",mProfile.getLikes());
        mRepository.updateProfile(otherPofile.getUid(),"likes",otherPofile.getLikes());


        notifyOtherProfile(otherPofile.getMessageToken()); // only for test
    }

    public void writeMyProfile() {
        mRepository.writeMyProfile(mProfile);
    }

    public Profile getProfile()
    {
        return mProfile;
    }

    public void notifyOtherProfile(String to) {
        JSONObject rootObject=new JSONObject();
        JSONObject dataObject=new JSONObject();
        try {
            rootObject.put("to",to);
            dataObject.put("match_uid",mProfile.getUid());
            dataObject.put("sender",mProfile.getFirstName());
            dataObject.put("image",mProfile.getProfilePictureUri());
            rootObject.put("data",dataObject);

            Log.d("notif",mProfile.getProfilePictureUri());
            NotificationManager.sendNotification(context,rootObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void setContext(Context context) {
        this.context=context;
    }


    public List<Profile> getProfiles() {
        return mProfiles;
    }

   public void readProfilesForGuest(){
       mRepository.readProfilesForGuest();
   }

    public boolean isLoginAsGuest() {
        return mIsLoginAsGuest;
    }

    public void setIsLoginAsGuest(boolean mIsLoginAsGuest) {
        this.mIsLoginAsGuest = mIsLoginAsGuest;
    }

    public List<Profile> getGuestProfiles() {
       return mGuestProfilesMutableLiveData.getValue();
    }
}
