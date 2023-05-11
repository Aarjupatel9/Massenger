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
    @Query("SELECT * FROM massege")
    List<MassegeEntity> getAllChatMaster();

    @Query("SELECT * FROM massege WHERE (receiver_id IN (:receiver_id) or sender_id IN(:receiver_id)) order by time_of_send")
    List<MassegeEntity> getChat(long receiver_id);

    @Query("SELECT * FROM massege WHERE (receiver_id IN (:receiver_id) and sender_id IN(:receiver_id)) order by time_of_send")
    List<MassegeEntity> getSelfChat(long receiver_id);

    @Query("SELECT * FROM massege WHERE massege_status=5")
    List<MassegeEntity> getOfflineStateMassege();

    @Query("UPDATE massege SET massege_status=:massege_status WHERE Chat_id = :chat_id")
    int updateMassegeStatus(long chat_id, int massege_status);

    @Query("UPDATE massege SET massege_status=:massege_status WHERE (Chat_id = :chat_id and MassegeID=:MassegeId)")
    int updateMassegeStatus(long MassegeId, long chat_id, int massege_status);

    @Query("UPDATE massege SET massege_status=:massege_status WHERE (sender_id = :SenderId and receiver_id=:ReceiverId and time_of_send=:sentTime)")
    int updateMassegeStatus(long SenderId, long ReceiverId,long sentTime, int massege_status);

    @Query("delete from massege where  (receiver_id IN (:CID) or sender_id IN(:CID)) ")
    int removeChatsFromMassegeTable(long CID);

    @Insert
    void insertMassegeIntoChat(MassegeEntity massegeEntity);

    @Insert
    void insertAllMassegeIntoChat(MassegeEntity... massegeEntities);

    @Delete
    void deleteMassegeOfChat(MassegeEntity massegeEntity);

    @Query("SELECT * FROM massege WHERE MassegeID=:MassegeID")
    MassegeEntity getMassegeByMassegeID(long MassegeID);

    @Query("SELECT * FROM massege WHERE time_of_send=:TimeOfSend and sender_id=:SenderId")
    MassegeEntity getMassegeByTimeOfSend(long SenderId, long TimeOfSend);

    @Query("Update massege set MassegeID=:MassegeNumber  WHERE Chat_id=:ChatId")
    int updateMassegeNumber(long ChatId, long MassegeNumber);

    @Query("SELECT * FROM massege where MassegeID=:MassegeId")
    List<MassegeEntity> getMassegeWithMassegeId(long MassegeId);

    //fro Login activity
    @Query("SELECT * FROM login limit 1")
    loginDetailsEntity getLoginDetailsFromDatabase();

    @Insert
    void SaveLoginDetailsInDatabase(loginDetailsEntity loginDetailsEntity);

    @Delete
    void deleteLoginDetailsInDatabase(loginDetailsEntity loginDetailsEntity);

    @Query("UPDATE login SET DisplayUserName = :displayUserName WHERE U_ID = :u_id")
    int updateDisplayUserName(String displayUserName, long u_id);

    //for logout
    @Query("delete from login where U_ID = :user_login_id")
    void LogOutFromAppForThisUser(long user_login_id);
//
//    @Query("delete from  contactDetails ")
//    void DeleteAllContactDetails(int user_login_id);
//    @Query("TRUNCATE from massege ")
//    void DeleteAllMasseges(int user_login_id);


    //geting user_id for app
    @Query("SELECT U_ID FROM login limit 1")
    long getUserIdFromDatabase();

    @Query("SELECT MobileNumber FROM login limit 1")
    long getUserMobileNumber();


    //contact related query
    @Query("SELECT * FROM contactDetails order by PriorityRank DESC")
    List<ContactWithMassengerEntity> getContactDetailsFromDatabase();

    @Query("SELECT * FROM contactDetails where C_ID=:CID")
    ContactWithMassengerEntity getContactWith_CID(long CID);

    @Query("SELECT PriorityRank FROM contactDetails order by PriorityRank desc limit 1 ")
    long getHighestPriorityRank();

    @Query("update contactDetails set PriorityRank=:PriorityRank where C_ID=:C_ID")
    void setPriorityRank(long C_ID, long PriorityRank);

    @Insert
    void SaveAllContactDetailsInDatabase(ContactWithMassengerEntity... ContactWithMassenger_entities);

    @Insert
    void SaveContactDetailsInDatabase(ContactWithMassengerEntity ContactWithMassengerEntity);

    @Query("Delete From contactDetails Where MobileNumber=(:number)")
    void deleteContactDetailsInDatabase(Long number);

    @Query("Delete From contactDetails Where C_ID=(:CID)")
    int removeSelfContactFromContactTable(long CID);

    @Query("SELECT Chat_id From massege ORDER BY Chat_id DESC LIMIT 1")
    int getLastInsertedMassege();

    @Query("update contactDetails set NewMassegeArriveValue=(:value) where C_ID=(:cId)")
    void updateNewMassegeArriveValue(long cId, int value);

    @Query("update contactDetails set NewMassegeArriveValue=NewMassegeArriveValue+1 where C_ID=(:cId)")
    void incrementNewMassegeArriveValue(long cId);


    //for first time setup
    @Insert
    void addAppOpenDetails(SetupFirstTimeEntity setupFirstTimeEntity);

    @Query("SELECT * From app_open_details")
    List<SetupFirstTimeEntity> getListOfAppOpenDetails();

    @Query("SELECT * From app_open_details order by appOpenNumber DESC limit 1")
    SetupFirstTimeEntity getAppOpenDetailsForFirstTime();


    @Query("update allContactDetails set C_ID=:CID where MobileNumber=:number")
    int updateAllContactOfUserEntityC_ID(long number, long CID);

    @Query("SELECT * from ALLCONTACTDETAILS where mobilenumber=:number")
    List<AllContactOfUserEntity> getSelectedAllContactOfUserEntity(long number);

    @Update
    int updateAllContactOfUserEntity(AllContactOfUserEntity allContactOfUserEntity);

    @Insert
    void addAllContactOfUserEntity(AllContactOfUserEntity allContactOfUserEntity);
}
