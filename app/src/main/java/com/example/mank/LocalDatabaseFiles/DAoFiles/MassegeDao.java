package com.example.mank.LocalDatabaseFiles.DAoFiles;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;
import com.example.mank.LocalDatabaseFiles.entities.SetupFirstTimeEntity;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;


import java.util.List;

@Dao
public interface MassegeDao {


    //massege related query

    @Query("SELECT * FROM massege WHERE ((ReceiverId IN (:ReceiverId) or SenderId IN(:ReceiverId)) and AppUserId=:appUserId )order by timeOfSend")
    List<MassegeEntity> getChat(String ReceiverId,String appUserId);

    @Query("SELECT * FROM massege WHERE ((ReceiverId IN (:ReceiverId) and SenderId IN(:ReceiverId)) and AppUserId=:appUserId) order by timeOfSend")
    List<MassegeEntity> getSelfChat(String ReceiverId , String appUserId);


    @Query("SELECT * FROM massege WHERE (massegeStatus=:status and AppUserId=:appUserId) ")
    List<MassegeEntity> getMassegesWithStatus(int status, String appUserId);

    @Query("SELECT massegeStatus FROM massege WHERE (SenderId=:senderId and ReceiverId=:receiverId and timeOfSend=:time and AppUserId=:appUserId)")
    int getMassegeStatus(String senderId, String receiverId, long time, String appUserId);



    @Query("UPDATE massege SET massegeStatus=:massegeStatus WHERE (SenderId = :SenderId and ReceiverId=:ReceiverId and timeOfSend=:sentTime and AppUserId=:appUserId)")
    int updateMassegeStatus(String SenderId, String ReceiverId, long sentTime, int massegeStatus, String appUserId);

    @Query("delete from massege where ((ReceiverId IN (:CID) or SenderId IN(:CID)) and AppUserId=:appUserId)")
    int removeChatsFromMassegeTable(String CID, String appUserId);

    // inbuilt function of room
    @Insert
    void insertMassegeIntoChat(MassegeEntity massegeEntity);

    //select queries

    @Query("SELECT * FROM massege WHERE (timeOfSend=:TimeOfSend and SenderId=:SenderId and AppUserId=:appUserId)")
    MassegeEntity getMassegeByTimeOfSend(String SenderId, long TimeOfSend, String appUserId);

    @Query("SELECT * FROM massege where AppUserId=:appUserId")
    List<MassegeEntity> getMassegeByAppUserId(String appUserId);


    //fro Login activity
    @Query("SELECT * FROM login limit 1")
    loginDetailsEntity getLoginDetailsFromDatabase();

    @Insert
    void SaveLoginDetailsInDatabase(loginDetailsEntity loginDetailsEntity);

    @Query("UPDATE login SET DisplayUserName = :displayUserName WHERE UID = :u_id")
    int updateDisplayUserName(String displayUserName, String u_id);
    @Query("UPDATE login SET About=:about WHERE UID = :u_id")
    int updateAboutUserName(String about, String u_id);

    //for logout
    @Query("delete from login where UID =:appUserId")
    void LogOutFromAppForThisUser(String appUserId);

    //getting user_id for app
    @Query("SELECT UID FROM login limit 1")
    String getUserIdFromDatabase();

    @Query("SELECT MobileNumber FROM login limit 1")
    long getUserMobileNumber();


    //contact related query
    @Query("SELECT * FROM contactDetails where AppUserId=:appUserId order by PriorityRank DESC")
    List<ContactWithMassengerEntity> getContactDetailsFromDatabase(String appUserId);

    @Query("SELECT * FROM contactDetails where CID=:CID and AppUserId=:appUserId")
    ContactWithMassengerEntity getContactWith_CID(String CID, String appUserId);

    @Query("SELECT PriorityRank FROM contactDetails where AppUserId=:appUserId order by PriorityRank desc limit 1 ")
    long getHighestPriorityRank(String appUserId);

    @Query("update contactDetails set PriorityRank=:PriorityRank where CID=:CID and AppUserId=:appUserId")
    void setPriorityRank(String CID, long PriorityRank,String appUserId);

    @Query("update contactDetails set UserImage=:userImage where (CID=:CID and AppUserId=:appUserId)")
    int updateImageIntoContactDetails(String CID, byte[] userImage,String appUserId);

    @Query("select UserImage from contactDetails  where CID=:CID and AppUserId=:appUserId")
    byte[] getSelfUserImage(String CID, String appUserId);



    @Insert
    void SaveContactDetailsInDatabase(ContactWithMassengerEntity ContactWithMassengerEntity);

    @Query("Delete From contactDetails Where (MobileNumber=(:number) and AppUserId=:appUserId)")
    void deleteContactDetailsInDatabase(Long number, String appUserId);

    @Query("Delete From contactDetails Where CID=(:CID) and AppUserId=:appUserId")
    int removeSelfContactFromContactTable(String CID, String appUserId);

    @Query("SELECT ChatId From massege where AppUserId=:appUserId ORDER BY ChatId DESC LIMIT 1")
    int getLastInsertedMassege(String appUserId);

    @Query("update contactDetails set NewMassegeArriveValue=(:value) where CID=(:cId) and AppUserId=:appUserId")
    void updateNewMassegeArriveValue(String cId, int value, String appUserId);

    @Query("update contactDetails set NewMassegeArriveValue=NewMassegeArriveValue+1 where CID=(:cId) and AppUserId=:appUserId")
    void incrementNewMassegeArriveValue(String cId, String appUserId);


    //for first time setup
    @Insert
    void addAppOpenDetails(SetupFirstTimeEntity setupFirstTimeEntity);

    @Query("SELECT * From app_open_details")
    List<SetupFirstTimeEntity> getListOfAppOpenDetails();

    @Query("SELECT * From app_open_details order by appOpenNumber DESC limit 1")
    SetupFirstTimeEntity getLastAppOpenEntity();

    @Insert
    void insertLastAppOpenEntity(SetupFirstTimeEntity setupFirstTimeEntity);


    @Query("update allContactDetails set CID=:CID where MobileNumber=:number and AppUserId=:appUserId")
    int updateAllContactOfUserEntityCID(long number, String CID, String appUserId);

    @Query("SELECT * from allContactDetails where mobilenumber=:number and AppUserId=:appUserId")
    List<AllContactOfUserEntity> getSelectedAllContactOfUserEntity(long number, String appUserId);

    @Update
    int updateAllContactOfUserEntity(AllContactOfUserEntity allContactOfUserEntity);

    @Insert
    void addAllContactOfUserEntity(AllContactOfUserEntity allContactOfUserEntity);


    @Query("select  `index`,time , CID,MobileNumber,DisplayName,ImageVersion from allContactDetails where AppUserId=:appUserId")
    List<AllContactOfUserEntity> getAllContactDetailsFromDB(String appUserId);

    @Query("select `index`,time,CID,MobileNumber,DisplayName,ImageVersion from allContactDetails where CID==-1 and AppUserId=:appUserId")
    List<AllContactOfUserEntity> getDisConnectedContactDetailsFromDB(String appUserId);

    @Query("select `index`,time ,CID,MobileNumber,DisplayName,ImageVersion from allContactDetails where CID>-1 and AppUserId=:appUserId")
    List<AllContactOfUserEntity> getConnectedContactDetailsFromDB(String appUserId);

    @Query("select `index`,time ,CID,MobileNumber,ImageVersion from allContactDetails where CID>-1 and AppUserId=:appUserId ")
    List<AllContactOfUserEntity> getConnectedContactImageList(String appUserId);


    @Query("update contactDetails set ProfileImageVersion=:ProfileImageVersion where CID=:CID and AppUserId=:appUserId")
    int updateProfileImageVersion(String CID, long ProfileImageVersion , String appUserId);
    @Query("select  ProfileImageVersion from  contactDetails where CID=:CID and AppUserId=:appUserId")
    long getContactProfileImageVersion(String CID, String appUserId);
}
