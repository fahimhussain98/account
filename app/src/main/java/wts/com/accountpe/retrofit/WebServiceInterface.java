package wts.com.accountpe.retrofit;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WebServiceInterface {

    @FormUrlEncoded
    @POST("UserSignUp2")
    Call<JsonObject> newUserSignUp(@Header("Authorization") String auth,
                                   @Field("name") String name,
                                   @Field("dob") String dob,
                                   @Field("shopName") String shopName,
                                   @Field("mobileNo") String mobileno,
                                   @Field("emailId") String emailid,
                                   @Field("address") String address,
                                   @Field("pincode") String pincode,
                                   @Field("stateId") String stateId,
                                   @Field("cityId") String cityId,
                                   @Field("remark") String remark,
                                   @Field("PancardNo") String PancardNo,
                                   @Field("AddharNo") String AddharNo,
                                   @Field("appversion") String appversion,
                                   @Field("referralcode") String referralcode
    );

    @FormUrlEncoded
    @POST("UPIGateway")
    Call<JsonObject> upiGateway(@Header("Authorization") String auth,
                                @Field("userID") String userID,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("amount") String amount,
                                @Field("customerName") String customerName,
                                @Field("customerEmail") String customerEmail,
                                @Field("customerMobile") String customerMobile);

    @FormUrlEncoded
    @POST("UserMoneyTransferReport")
    Call<JsonObject> getDmtReport(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("fromDate") String fromDate,
                                  @Field("toDate") String toDate,
                                  @Field("Accountno") String Accountno);

    @FormUrlEncoded
    @POST("UserWalletToWalletReport")
    Call<JsonObject> walletToWalletReport(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("fromDate") String fromDate,
                                  @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("BeneRegistration")
    Call<JsonObject> addBeneficiary(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("remitterId") String remitterId,
                                    @Field("bankName") String bankName,
                                    @Field("beneFirstName") String beneFirstName,
                                    @Field("beneLastName") String beneLastName,
                                    @Field("mobileNo") String mobileNo,
                                    @Field("ifscCode") String ifscCode,
                                    @Field("accountNo") String accountNo,
                                    @Field("senderMobileNo") String senderMobileNo,
                                    @Field("address") String address,
                                    @Field("pinCode") String pinCode,
                                    @Field("otp") String otp);

    @FormUrlEncoded
    @POST("AddUPIforDMT")
    Call<JsonObject> addDmtUpi(@Header("Authorization") String auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo,
                               @Field("remitterId") String remitterId,
                               @Field("beneFirsName") String beneFirsName,
                               @Field("mobileNo") String mobileNo,
                               @Field("ifscCode") String ifscCode,
                               @Field("upiId") String upiId);

    @FormUrlEncoded
    @POST("VerifyUPIDMT")
    Call<JsonObject> verifyUpiId(@Header("Authorization") String auth,
                                 @Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo,
                                 @Field("remitterId") String remitterId,
                                 @Field("beneName") String beneName,
                                 @Field("mobile") String mobile,
                                 @Field("upiId") String upiId,
                                 @Field("senderMobileNo") String senderMobileNo,
                                 @Field("senderName") String senderName);

    @FormUrlEncoded
    @POST("DMTaccountVarify")
    Call<JsonObject> verifyAccount(@Header("Authorization") String auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("remitterId") String remitterId,
                                   @Field("bankName") String bankName,
                                   @Field("beneFirstName") String beneFirstName,
                                   @Field("beneLastName") String beneLastName,
                                   @Field("mobileNo") String mobileNo,
                                   @Field("ifscCode") String ifscCode,
                                   @Field("accountNo") String accountNo,
                                   @Field("senderMobileNo") String senderMobileNo,
                                   @Field("address") String address,
                                   @Field("pinCode") String pinCode,
                                   @Field("otp") String otp,
                                   @Field("senderName") String senderName,
                                   @Field("transactionMode") String transactionMode);

    @FormUrlEncoded
    @POST("DeleteBeneficiary")
    Call<JsonObject> deleteBeneficiary(@Header("Authorization") String auth,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo,
                                       @Field("userID") String userID,
                                       @Field("beneficiaryId") String beneficiaryId,
                                       @Field("senderMobileNo") String senderMobileNo,
                                       @Field("remitterId") String remitterId);

    @FormUrlEncoded
    @POST("TransferMoney")
    Call<JsonObject> payBeneficiary(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("txnAmount") String txnAmount,
                                    @Field("beneficiaryId") String beneficiaryId,
                                    @Field("outletId") String outletId,
                                    @Field("senderName") String senderName,
                                    @Field("senderMobileNo") String senderMobileNo,
                                    @Field("accountNo") String accountNo,
                                    @Field("beneficiaryName") String beneficiaryName,
                                    @Field("bankName") String bankName,
                                    @Field("ifscCode") String ifscCode,
                                    @Field("beneMobileNo") String beneMobileNo,
                                    @Field("transactionMode") String transactionMode,
                                    @Field("bankId") String bankId
    );

    @FormUrlEncoded
    @POST("transferMoneyUPI")
    Call<JsonObject> payUpi(@Header("Authorization") String auth,
                            @Field("userID") String userID,
                            @Field("tokenKey") String tokenKey,
                            @Field("deviceInfo") String deviceInfo,
                            @Field("txnAmount") String txnAmount,
                            @Field("beneficiaryId") String beneficiaryId,
                            @Field("senderName") String senderName,
                            @Field("senderMobileNo") String senderMobileNo,
                            @Field("accountNo") String accountNo,
                            @Field("beneficiaryName") String beneficiaryName,
                            @Field("beneMobileNo") String beneMobileNo,
                            @Field("transactionMode") String transactionMode,
                            @Field("latitude") String latitude,
                            @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST("AddSender")
    Call<JsonObject> addSender(@Header("Authorization") String auth,
                               @Field("tokenKey") String token,
                               @Field("deviceInfo") String DeviceInfo,
                               @Field("userID") String userID,
                               @Field("senderMobileNo") String sendermobileno,
                               @Field("firstName") String firstname,
                               @Field("lastName") String lastname,
                               @Field("pinCode") String pincode,
                               @Field("address") String address,
                               @Field("otp") String otp);

    @FormUrlEncoded
    @POST("SenderOtpVarify")
    Call<JsonObject> verifySender(@Header("Authorization") String auth,
                                  @Field("tokenKey") String token,
                                  @Field("deviceInfo") String DeviceInfo,
                                  @Field("remitterId") String remitterId,
                                  @Field("userID") String userID,
                                  @Field("senderMobileNo") String senderMobileNo,
                                  @Field("otp") String otp);

    @FormUrlEncoded
    @POST("GetSender")
    Call<JsonObject> isUserValidate(@Header("Authorization") String auth,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("userID") String userid,
                                    @Field("senderMobileNo") String SenderMobsenderMobileNoile);

    @FormUrlEncoded
    @POST("UserSettlementReport")
    Call<JsonObject> getSettlementReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate,
                                         @Field("Accountno") String Accountno);

    @FormUrlEncoded
    @POST("GetUserAepsReport")
    Call<JsonObject> getAepsReport(@Header("Authorization") String Auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("fromDate") String fromDate,
                                   @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("Add-Payoutbank")
    Call<JsonObject> addbankDetails(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("BankName") String BankName,
                                    @Field("AccountNo") String AccountNo,
                                    @Field("AccHolderName") String AccHolderName,
                                    @Field("IfscCode") String IfscCode);

    @FormUrlEncoded
    @POST("GetBankListForDMT")
    Call<JsonObject> getBankDmt(@Header("Authorization") String auth,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("userID") String userID);

    @FormUrlEncoded
    @POST("SettlementBankMaster")
    Call<JsonObject> getAccountDetails(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo);


    @POST("GetUPIID")
    Call<JsonObject> getUpiId(@Header("Authorization") String Auth);

    @FormUrlEncoded
    @POST("WTSUPIGateWay")
    Call<JsonObject> insertUPI_info(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("Name") String Name,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("amount") String amount,
                                    @Field("upiName") String upiName);

    @FormUrlEncoded
    @POST("UpdateWTSUpiGatewayStatus")
    Call<JsonObject> updateUPI_info(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("UniqueTransactionId") String UniqueTransactionId,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("TransactionId") String TransactionId,
                                    @Field("Status") String Status,
                                    @Field("response") String response);

    @FormUrlEncoded
    @POST("CheckCredientials")
    Call<JsonObject> checkCredential(@Header("Authorization") String auth,
                                     @Field("userName") String userName,
                                     @Field("password") String password,
                                     @Field("deviceID") String deviceID,
                                     @Field("appversion") String appversion);

    @FormUrlEncoded
    @POST("DoOfflineUserOnboard")
    Call<JsonObject> checkOfflineUserOnboard(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("event") String event);
    @FormUrlEncoded
    @POST("DoOfflineUserOnboard")
    Call<JsonObject> doOfflineUserOnboard(@Header("Authorization") String auth,
                                             @Field("userID") String userID,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("event") String event,
                                             @Field("nameAsAadhar") String nameAsAadhar,
                                             @Field("nameAsPan") String nameAsPan,
                                             @Field("fatherName") String fatherName,
                                             @Field("dobAsAadhar") String dobAsAadhar,
                                             @Field("imgAsAadhar") String imgAsAadhar,
                                             @Field("gender") String gender,
                                             @Field("pancardNo") String pancardNo,
                                             @Field("aadharNo") String aadharNo,
                                             @Field("aadharFrntImg") String aadharFrntImg,
                                             @Field("aadharBackImg") String aadharBackImg,
                                             @Field("shope1Img") String shope1Img,
                                             @Field("shope2Img") String shope2Img,
                                             @Field("panCardImg") String panCardImg,
                                             @Field("agreement1Img") String agreement1Img,
                                             @Field("agreement2Img") String agreement2Img
    );
    @FormUrlEncoded
    @POST("DoOfflineUserOnboard")
    Call<JsonObject> doOfflineUploadDocument(@Header("Authorization") String auth,
                                          @Field("userID") String userID,
                                          @Field("tokenKey") String tokenKey,
                                          @Field("deviceInfo") String deviceInfo,
                                          @Field("event") String event,
                                          @Field("aadharFrntImg") String aadharFrntImg,
                                          @Field("aadharBackImg") String aadharBackImg,
                                          @Field("shope1Img") String shope1Img,
                                          @Field("shope2Img") String shope2Img,
                                          @Field("panCardImg") String panCardImg,
                                          @Field("agreement1Img") String agreement1Img,
                                          @Field("agreement2Img") String agreement2Img
    );

    @FormUrlEncoded
    @POST("UserUtilityReport")
    Call<JsonObject> getBBPSReport(@Header("Authorization") String auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("fromDate") String fromDate,
                                   @Field("toDate") String toDate,
                                   @Field("ServiceName") String ServiceName);

    @FormUrlEncoded
    @POST("BbpsBillFetch")
    Call<JsonObject> fetchBill(@Header("Authorization") String auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo,
                               @Field("ServiceType") String ServiceType,
                               @Field("serviceId") String serviceId,
                               @Field("operatorId") String operatorId,
                               @Field("consumerNumber") String consumerNumber,
                               @Field("mobileNo") String mobileNo,
                               @Field("subDivisionCode") String subDivisionCode,
                               @Field("dateOfBirth") String dateOfBirth             //  dob use only for bill avenue bbps  12/12/23
    );

    @FormUrlEncoded
    @POST("BbpsBillPay")
    Call<JsonObject> payBill(@Header("Authorization") String auth,
                             @Field("userID") String userID,
                             @Field("tokenKey") String tokenKey,
                             @Field("deviceInfo") String deviceInfo,
                             @Field("ServiceType") String ServiceType,
                             @Field("operatorId") String operatorId,
                             @Field("consumerNumber") String consumerNumber,
                             @Field("mobileNo") String mobileNo,
                             @Field("consumerName") String consumerName,
                             @Field("billAmt") String billAmt,
                             @Field("dueDate") String dueDate,
                             @Field("serviceId") String serviceId,
                             @Field("ad2") String ad2,
                             @Field("ad3") String ad3,
                             @Field("CompleteBillFetch") String CompleteBillFetch,
                             @Field("requestId") String requestId,
                             @Field("subDivisionCode") String subDivisionCode,
                             @Field("billalidationId") String billalidationId,
                             @Field("billerinfo") String billerinfo,
                             @Field("inputinfo") String inputinfo,
                             @Field("additionalInfo") String additionalInfo,
                             @Field("dateOfBirth") String dateOfBirth  //  dob use only for bill avenue bbps  12/12/23
    );

    @FormUrlEncoded
    @POST("BBPSBillPayViaGateway")
    Call<JsonObject> payBillViaGateway(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo,
                                       @Field("ServiceType") String ServiceType,
                                       @Field("operatorId") String operatorId,
                                       @Field("consumerNumber") String consumerNumber,
                                       @Field("mobileNo") String mobileNo,
                                       @Field("consumerName") String consumerName,
                                       @Field("billAmt") String billAmt,
                                       @Field("dueDate") String dueDate,
                                       @Field("billalidationId") String billalidationId,
                                       @Field("subDivisionCode") String subDivisionCode,
                                       @Field("serviceId") String serviceId,
                                       @Field("payVia") String payVia,
                                       @Field("walletBalance") String walletBalance,
                                       @Field("gatewayAmount") String gatewayAmount,
                                       @Field("PgResponseAmt") String PgResponseAmt,
                                       @Field("getewayUniqueTxnId") String getewayUniqueTxnId
    );

    @FormUrlEncoded
    @POST("GetSubdivionCode")
    Call<JsonObject> getSubDivisionCode(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("GetNewsMaster")
    Call<JsonObject> getNews(@Header("Authorization") String auth,
                             @Field("userID") String userID,
                             @Field("tokenKey") String tokenKey,
                             @Field("deviceInfo") String deviceInfo,
                             @Field("userType") String userType);

    @FormUrlEncoded
    @POST("GetDashboardBanners")
    Call<JsonObject> getBanner(@Header("Authorization") String auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo
    );

    @FormUrlEncoded
    @POST("myCommission")
    Call<JsonObject> getMyCommission(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("CreditReport")
    Call<JsonObject> getCreditReport(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("searchBy") String searchBy,
                                     @Field("fromDate") String fromDate,
                                     @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("DebitReport")
    Call<JsonObject> getDebitReport(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("searchBy") String searchBy,
                                    @Field("fromDate") String fromDate,
                                    @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("SalesReport")
    Call<JsonObject> getCommissionReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("SearchMode") String SearchMode,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate);


    @FormUrlEncoded
    @POST("MainLedger")
    Call<JsonObject> getLedgerReport(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("Accountno") String Accountno,
                                     @Field("fromDate") String fromDate,
                                     @Field("toDate") String toDate
    );

    @FormUrlEncoded
    @POST("UserAepsLedgerReport")
    Call<JsonObject> getAepsLedgerReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("Accountno") String Accountno,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("ChangePassword")
    Call<JsonObject> changePassword(@Header("Authorization") String auth,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("userID") String userID,
                                    @Field("oldPassword") String oldPassword,
                                    @Field("newPassword") String newPassword);

    @FormUrlEncoded
    @POST("GetStateList")
    Call<JsonObject> getState(@Header("Authorization") String Auth,
                              @Field("userID") String userID,
                              @Field("tokenKey") String tokenKey,
                              @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("GetCityList")
    Call<JsonObject> getCity(@Header("Authorization") String Auth,
                             @Field("userID") String userID,
                             @Field("tokenKey") String tokenKey,
                             @Field("deviceInfo") String deviceInfo,
                             @Field("stateID") String stateID);

    @FormUrlEncoded
    @POST("GetSchemeList")
    Call<JsonObject> getScheme(@Header("Authorization") String Auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo,
                               @Field("action") String action,
                               @Field("userType") String userType,
                               @Field("iD") String iD);

    @FormUrlEncoded
    @POST("AddUsers")
    Call<JsonObject> addUser(@Header("Authorization") String Auth,
                             @Field("userID") String userID,
                             @Field("tokenKey") String tokenKey,
                             @Field("deviceInfo") String deviceInfo,
                             @Field("mobileNo") String mobileNo,
                             @Field("password") String password,
                             @Field("name") String name,
                             @Field("userType") String userType,
                             @Field("emailID") String emailID,
                             @Field("dob") String dob,
                             @Field("companyName") String companyName,
                             @Field("address") String address,
                             @Field("stateID") String stateID,
                             @Field("cityID") String cityID,
                             @Field("pancardNo") String pancardNo,
                             @Field("aadharNo") String aadharNo,
                             @Field("schemeID") String schemeID,
                             @Field("capBal") String capBal);

    @Multipart
    @POST("FileUploading/UploadFile")
    Call<JsonObject> uploadfile(@Header("Authorization") String auth,
                                @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("FundRequest")
    Call<JsonObject> doPaymentRequest(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("requestDate") String requestDate,
                                      @Field("referenceNo") String referenceNo,
                                      @Field("bankName") String bankName,
                                      @Field("paymentMode") String paymentMode,
                                      @Field("remark") String remark,
                                      @Field("imagePath") String imagePath,
                                      @Field("requestTo") String requestTo,
                                      @Field("amount") String amount);

    @FormUrlEncoded
    @POST("GetChieldList")
    Call<JsonObject> getUsers(@Header("Authorization") String auth,
                              @Field("tokenKey") String tokenKey,
                              @Field("deviceInfo") String deviceInfo,
                              @Field("userID") String userid,
                              @Field("action") String action);

    @FormUrlEncoded
    @POST("DebitAmount")
    Call<JsonObject> doDebitBalance(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("amount") String amount,
                                    @Field("chieldID") String chieldID);

    @FormUrlEncoded
    @POST("CreditAmount")
    Call<JsonObject> doCreditBalance(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("amount") String amount,
                                     @Field("chieldID") String chieldID);

    @FormUrlEncoded
    @POST("GetBankDetails")
    Call<JsonObject> getBankList(@Header("Authorization") String auth,
                                 @Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("DoComplain")
    Call<JsonObject> makeComplaint(@Header("Authorization") String auth,
                                   @Field("userID") String userID,
                                   @Field("tokenKey") String tokenKey,
                                   @Field("deviceInfo") String deviceInfo,
                                   @Field("UniqueId") String UniqueId,
                                   @Field("remarks") String remarks,
                                   @Field("ComplainType") String ComplainType,
                                   @Field("LiveId") String LiveId,
                                   @Field("serviceName") String serviceName,
                                   @Field("TxnDate") String TxnDate
    );

    @FormUrlEncoded
    @POST("RechargeReport")
    Call<JsonObject> getReport(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("userType") String userType,
            @Field("amountWise") String amountWise,
            @Field("statusWise") String statusWise,
            @Field("modeWise") String modeWise,
            @Field("mobileNo") String mobileNo,
            @Field("fromDate") String fromDate,
            @Field("toDate") String toDate,
            @Field("parentWise") String parentWise,
            @Field("operatorWise") String operatorWise,
            @Field("ServiceId") String ServiceId);

    @FormUrlEncoded
    @POST("GetService")
    Call<JsonObject> getServices(
            @Header("Authorization") String auth,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("userID") String userID);

    @FormUrlEncoded
    @POST("GetOperatorsList")
    Call<JsonObject> getOperators(@Header("Authorization") String auth,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("userID") String userID,
                                  @Field("serviceID") String serviceID);

    @FormUrlEncoded
    @POST("DoDTHRecharge")
    Call<JsonObject> doDthRecharge(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("OperatorId") String OperatorId,
            @Field("Amount") String Amount,
            @Field("MobileNo") String MobileNo,
            @Field("ServiceType") String ServiceType);

    @FormUrlEncoded
    @POST("DoRecharge")
    Call<JsonObject> doRecharge(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("OperatorId") String OperatorId,
            @Field("Amount") String Amount,
            @Field("MobileNo") String MobileNo,
            @Field("ServiceType") String ServiceType);

    @FormUrlEncoded
    @POST("PostPaidQuickPay")
    Call<JsonObject> postPaidQuickPay(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("OperatorId") String OperatorId,
                                      @Field("Amount") String Amount,
                                      @Field("MobileNo") String MobileNo);

    @FormUrlEncoded
    @POST("DoRechargeViaGateway")
    Call<JsonObject> doRechargeViaGateway(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("OperatorId") String OperatorId,
            @Field("Amount") String Amount,
            @Field("MobileNo") String MobileNo,
            @Field("ServiceType") String ServiceType,
            @Field("payVia") String payVia,
            @Field("walletBalance") String walletBalance,
            @Field("gatewayAmount") String gatewayAmount,
            @Field("PgResponseAmt") String PgResponseAmt,
            @Field("getewayUniqueTxnId") String getewayUniqueTxnId
    );

    @FormUrlEncoded
    @POST("TxnConfirmationPopup")
    Call<JsonObject> getCommissionCalculation(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("OperatorId") String OperatorId,
            @Field("Amount") String Amount,
            @Field("ServiceId") String ServiceId);


    @FormUrlEncoded
    @POST("MplanDTHMnpCheck")
    Call<JsonObject> getMNpForDTH(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("mobileNumber") String mobileNumber
    );

    @FormUrlEncoded
    @POST("MPlanGetMnp")
    Call<JsonObject> getMNp(@Header("Authorization") String auth,
                            @Field("userID") String userID,
                            @Field("tokenKey") String tokenKey,
                            @Field("deviceInfo") String deviceInfo,
                            @Field("mobileNumber") String mobileNumber
    );

    /*
    @FormUrlEncoded
    @POST("UserLogin")
    Call<JsonObject> login(
            @Header("Authorization") String auth,
            @Field("userName") String userName,
            @Field("password") String password,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("appversion") String appVersion
    );
    */
    @FormUrlEncoded
    @POST("ValidateAuthentication")
    Call<JsonObject> validateAuthentication(@Header("Authorization") String auth,
                                            @Field("userID") String userID,
                                            @Field("tokenKey") String tokenKey,
                                            @Field("deviceInfo") String deviceInfo,
                                            @Field("authenticateCode") String authenticateCode);

    @FormUrlEncoded
    //  @POST("UserLogin")
    @POST("DemoLogin")
    Call<JsonObject> login(
            @Header("Authorization") String auth,
            @Field("userName") String userName,
            @Field("password") String password,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("appversion") String appVersion,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("deviceName") String deviceName
    );

    @FormUrlEncoded
    @POST("LoginwithOTP")
    Call<JsonObject> loginWithOtp(
            @Header("Authorization") String auth,  // only for login
            @Field("mobileno") String mobileno);

    @FormUrlEncoded
    @POST("GetUserBalance")
    Call<JsonObject> getBalance(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("action") String action,
            @Field("balUserID") String balUserID);

    @FormUrlEncoded
    @POST("GetUserAepsBalance")
    Call<JsonObject> getAepsBalance(
            @Header("Authorization") String auth,
            @Field("userID") String userID,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("action") String action,
            @Field("balUserID") String balUserID);

    @FormUrlEncoded
    @POST("ForgetPassword")
    Call<JsonObject> forgetPassword(@Header("Authorization") String auth,
                                    @Field("userName") String username,
                                    @Field("mobileNo") String mobileno);

    // mpin
    @FormUrlEncoded
    @POST("GetOtpSecurity")
    Call<JsonObject> getOtp(@Header("Authorization") String auth,
                            @Field("userID") String userID,
                            @Field("tokenKey") String tokenKey,
                            @Field("deviceInfo") String deviceInfo,
                            @Field("emailId") String emailId,
                            @Field("mobileNo") String mobileNo);

    @FormUrlEncoded
    @POST("Generate-Security")
    Call<JsonObject> generateMpin(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("securityType") String securityType,
                                  @Field("securityPin") String securityPin);

    @FormUrlEncoded
    @POST("ChangeMPINPassword")
    Call<JsonObject> ChangeMPINPassword
                                  (@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("appversion") String appversion,
                                  @Field("AdharNO") String AdharNO,
                                  @Field("EmailId") String EmailId,
                                  @Field("MobileNo") String MobileNo
                                  );

    @FormUrlEncoded
    @POST("ChangeTPINPassword")
    Call<JsonObject> ChangeTpin(@Header("Authorization") String auth,
                                @Field("userID") String userID,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("appversion") String appversion,
                                @Field("MobileNo") String mobileNo,
                                @Field("AdharNO") String adharNO,
                                @Field("EmailId") String emailId);

    @FormUrlEncoded
    @POST("GetSecurityMpin")
    Call<JsonObject> checkMpinOrTPIN(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("securityType") String securityType,
                                     @Field("securityPin") String securityPin);

    // mplan

    @FormUrlEncoded
    @POST("MplanGetDthPlan")
    Call<JsonObject> getDthMplan(@Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo,
                                 @Field("operatorName") String operatorName);

    @FormUrlEncoded
    @POST("MplanGetCustomerInfo")
    Call<JsonObject> getDthUserInfo(@Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("operatorName") String operatorName,
                                    @Field("dthNo") String dthNo);

    @FormUrlEncoded
    @POST("MplanMobileSpecialPlan")
    Call<JsonObject> getMyPlans(@Field("userID") String userID,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("operatorName") String operatorName,
                                @Field("mobileNumber") String mobileNumber);

    @FormUrlEncoded
    @POST("MplanMobileSimplePlan")
    Call<JsonObject> getPlans(@Field("userID") String userID,
                              @Field("tokenKey") String tokenKey,
                              @Field("deviceInfo") String deviceInfo,
                              @Field("operatorName") String operatorName,
                              @Field("circle") String circle);

    @FormUrlEncoded
    @POST("GetAllNotification")
    Call<JsonObject> getAllNotification(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("UpdatePushNotification")
    Call<JsonObject> markNotificationAsRead(@Header("Authorization") String auth,
                                            @Field("userID") String userID,
                                            @Field("tokenKey") String tokenKey,
                                            @Field("deviceInfo") String deviceInfo,
                                            @Field("notiid") String notiid);

    @FormUrlEncoded
    @POST("DeleteNotification")
    Call<JsonObject> deleteNotification(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo,
                                        @Field("notiid") String notiid);

    @FormUrlEncoded
    @POST("ComplaintStatusReport")
    Call<JsonObject> makeComplaintReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("UserUpiGateWay")
    Call<JsonObject> getAddMoneyReport(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo,
                                       @Field("fromDate") String amountWise,
                                       @Field("toDate") String statusWise

    );

    @FormUrlEncoded
    @POST("UPIGatewayReport")  //  for add money MRobotics qrcode collection
    Call<JsonObject> upigatewayreport(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("fromDate") String amountWise,
                                      @Field("toDate") String statusWise,
                                      @Field("searchBy") String searchBy

    );

    // paytm payment gateway

    @FormUrlEncoded
    @POST("GeneratePaytmCheckSum")
    Call<JsonObject> getCheckSum(@Header("Authorization") String auth,
                                 @Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo,
                                 @Field("UniqueId") String UniqueId,
                                 @Field("Amount") String Amount);


    ////   paytm payment gateway

    @FormUrlEncoded
    @POST("GetAePS3KYCStatus")
    Call<JsonObject> checkUserKycInstantPayStatus(@Header("Authorization") String auth,
                                                  @Field("userID") String userID,
                                                  @Field("tokenKey") String tokenKey,
                                                  @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("CheckAePSUserKyc")
    Call<JsonObject> checkUserKycStatus(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("WTSAePS2UserOnboard")
    Call<JsonObject> instantWtsUserOnboard(@Header("Authorization") String auth,
                                           @Field("userID") String userID,
                                           @Field("tokenKey") String tokenKey,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("merchantMobile") String merchantMobile,
                                           @Field("merchantPanNo") String merchantPanNo,
                                           @Field("merchantEmail") String merchantEmail,
                                           @Field("merchantAadhar") String merchantAadhar,
                                           @Field("latitude") String latitude,
                                           @Field("longitude") String longitude,
                                           @Field("merchantPincode") String merchantPincode,
                                           @Field("merchantName") String merchantName,
                                           @Field("merchantAddress") String merchantAddress,
                                           @Field("company") String company,
                                           @Field("isNew") String isNew,
                                           @Field("consent") String consent);

    @FormUrlEncoded
    @POST("WTSAePS2UserOnboardVerify")
    Call<JsonObject> wtsUserOnboardVerify(@Header("Authorization") String auth,
                                                 @Field("userID") String userID,
                                                 @Field("tokenKey") String tokenKey,
                                                 @Field("deviceInfo") String deviceInfo,
                                                 @Field("otpReferenceID") String otpReferenceID,
                                                 @Field("hash") String hash,
                                                 @Field("otp") String otp,
                                                 @Field("merchantMobile") String merchantMobile);



    // cashfree payment gateway

    @FormUrlEncoded
    @POST("GenerateToken_Cashfree")
    Call<JsonObject> generateCashFreeToken(@Header("Authorization") String auth,
                                           @Field("orderId") String orderId,
                                           @Field("orderAmount") String orderAmount,
                                           @Field("orderCurrency") String orderCurrency);

    @FormUrlEncoded
    @POST("InsertPGData")
    Call<JsonObject> insertData(@Header("Authorization") String auth,
                                @Field("userID") String userid,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("UniqueId") String UniqueId,
                                @Field("Amount") String Amount
    );

    @FormUrlEncoded
    @POST("UpdateCashfreeAddMoneyWebhook")
    Call<JsonObject> UpdateCashfreeAddMoneyWebhook(@Header("Authorization") String auth,
                                                   @Field("userID") String userid,
                                                   @Field("tokenKey") String tokenKey,
                                                   @Field("deviceInfo") String deviceInfo,
                                                   @Field("UniqueId") String UniqueId,
                                                   @Field("Status") String Status,
                                                   @Field("TransactionId") String TransactionId,
                                                   @Field("response") String response
    );

    @FormUrlEncoded
    @POST("GetPaymentGatewayReport")
    Call<JsonObject> getPaymentGatewayReport(@Header("Authorization") String auth,
                                             @Field("userID") String userID,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("fromDate") String amountWise,
                                             @Field("toDate") String statusWise,
                                             @Field("searchBy") String searchBy

    );

    ///////////////////////////////////////////////////// cashfree

    @FormUrlEncoded
    @POST("UserOfflinePaymentReport")
    Call<JsonObject> myFundRequestReport(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("userType") String userType,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate);


    @POST("getQRImage")
    Call<JsonObject> getQRImage(@Header("Authorization") String auth);

    // refer and earn

    @FormUrlEncoded
    @POST("SendReferralCode")
    Call<JsonObject> getReferEarnCode(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("OperatortId") String OperatortId
    );

    @FormUrlEncoded
    @POST("GetReferandEarnCommission")
    Call<JsonObject> getReferCommission(@Header("Authorization") String auth,
                                        @Field("userId") String userID,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo,
                                        @Field("serviceId") String action

    );

    // paysprint aeps

    @FormUrlEncoded
    @POST("PaySprintBankList")
    Call<JsonObject> getBankForAeps(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("DoPaySprintTransaction")
    Call<JsonObject> doAepsTransaction(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("aadharno") String aadharno,
                                       @Field("bankiinno") String bankiinno,
                                       @Field("amount") String amount,
                                       @Field("txntype") String txntype,
                                       @Field("fingerdata") String fingerdata,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude,
                                       @Field("mobileno") String mobileno,
                                       @Field("bankname") String bankname,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo);

    ///////////////////////////////////////////////////////////// paySprint aeps

    //paysprint settlement

    @FormUrlEncoded
    @POST("WPayouts")
    Call<JsonObject> paySprintMoveToBank(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("WalletType") String WalletType,
                                         @Field("Amount") String Amount,
                                         @Field("AccountNo") String AccountNo,
                                         @Field("BeniId") String BeniId,          // only for paySprint settlement
                                         @Field("ifscCode") String ifscCode,
                                         @Field("AccountType") String AccountType,
                                         @Field("TransactionType") String TransactionType,
                                         @Field("AcHolderName") String AcHolderName,
                                         @Field("apiname") String apiname,
                                         @Field("BankName") String bankName
    );

    // wts settlement

    @FormUrlEncoded
    @POST("WPayouts")
    Call<JsonObject> moveToBank(@Header("Authorization") String auth,
                                @Field("userID") String userID,
                                @Field("tokenKey") String tokenKey,
                                @Field("deviceInfo") String deviceInfo,
                                @Field("WalletType") String WalletType,
                                @Field("Amount") String Amount,
                                @Field("AccountNo") String AccountNo,
                                @Field("ifscCode") String ifscCode,
                                @Field("AccountType") String AccountType,
                                @Field("TransactionType") String TransactionType,
                                @Field("AcHolderName") String AcHolderName,
                                @Field("apiname") String apiname,
                                @Field("BankName") String bankName
    );

    @FormUrlEncoded
    @POST("GetPaySprintPayoutList")
    Call<JsonObject> getAccountDetailsPaySprint(@Header("Authorization") String auth,
                                                @Field("userID") String userID,
                                                @Field("tokenKey") String tokenKey,
                                                @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("PaySprintAddBank")
    Call<JsonObject> addBankDetailsPaySprint(@Header("Authorization") String auth,
                                             @Field("userID") String userID,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("account") String account,
                                             @Field("ifsc") String ifsc,
                                             @Field("name") String name,
                                             @Field("account_type") String account_type,
                                             @Field("bankname") String bankname,
                                             @Field("bankid") String bankid);

    @FormUrlEncoded
    @POST("PaysprintPayoutUploadDocument")
    Call<JsonObject> uploadbankDetails(@Header("Authorization") String auth,
                                       @Field("userID") String userID,
                                       @Field("tokenKey") String tokenKey,
                                       @Field("deviceInfo") String deviceInfo,
                                       @Field("panimage") String panimage,
                                       @Field("beneId") String beneId,
                                       @Field("passbookimage") String passbookimage);
    // user aadhar verification

    @FormUrlEncoded
    @POST("VerifyPanCard")
    Call<JsonObject> verifyPan(@Header("Authorization") String auth,
                               @Field("userID") String userID,
                               @Field("tokenKey") String tokenKey,
                               @Field("deviceInfo") String deviceInfo,
                               @Field("panNo") String panNo);

    @FormUrlEncoded
    @POST("ValidateAadharOTP")
    Call<JsonObject> verifyAadharOTP(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("tsTransID") String tsTransID,
                                     @Field("aadharNo") String aadharNo,
                                     @Field("otp") String otp);

    @FormUrlEncoded
    @POST("Verify-Aadhar")
    Call<JsonObject> verifyAadhar(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("aadharNo") String aadharNo);

    @FormUrlEncoded
    @POST("SendOTPWithMobileNo")
    Call<JsonObject> sendOTPWithMobileNo(@Header("Authorization") String auth,
                                         @Field("mobileno") String mobileno);

    @FormUrlEncoded
    @POST("UserChangeMobileNo")
    Call<JsonObject> editProfile(@Header("Authorization") String auth,
                                 @Field("userID") String userID,
                                 @Field("tokenKey") String tokenKey,
                                 @Field("deviceInfo") String deviceInfo,
                                 @Field("mobileNo") String mobileNo,
                                 @Field("oldmobileNo") String oldmobileNo,
                                 @Field("emailid") String emailid,
                                 @Field("oldemailid") String oldemailid,
                                 @Field("address") String address,
                                 @Field("name") String name);

    @FormUrlEncoded
    @POST("GetPopupBanner")
    Call<JsonObject> getPopupBanner(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("CheckServiceAssignStatus")
    Call<JsonObject> checkServicePermission(@Header("Authorization") String auth,
                                            @Field("userID") String userID,
                                            @Field("tokenKey") String tokenKey,
                                            @Field("deviceInfo") String deviceInfo,
                                            @Field("permissionId") String permissionId);

    @FormUrlEncoded
    @POST("User-WalletToWallet")
    Call<JsonObject> doWalletToWalletTransaction(@Header("Authorization") String auth,
                                                 @Field("userID") String userID,
                                                 @Field("tokenKey") String tokenKey,
                                                 @Field("deviceInfo") String deviceInfo,
                                                 @Field("amount") String amount,
                                                 @Field("transferto") String transferto);
    @FormUrlEncoded
    @POST("GetUserByMobileNo")
    Call<JsonObject> getUserDetails(@Header("Authorization") String auth,
                                    @Field("userID") String userID,
                                    @Field("tokenKey") String tokenKey,
                                    @Field("deviceInfo") String deviceInfo,
                                    @Field("MobileNo") String MobileNo);
    @FormUrlEncoded
    @POST("WalletToWallet")
    Call<JsonObject> walletAepsWalletToWalletTransaction(@Header("Authorization") String auth,
                                                         @Field("userID") String userID,
                                                         @Field("tokenKey") String tokenKey,
                                                         @Field("deviceInfo") String deviceInfo,
                                                         @Field("creditUserId") String creditUserId,
                                                         @Field("fromWallet") String fromWallet,
                                                         @Field("toWallet") String toWallet,
                                                         @Field("amount") String amount,
                                                         @Field("remarks") String remarks);

    //  instant pay aeps

    @FormUrlEncoded
    @POST("CheckInstantPayAepsKycStatus")
    Call<JsonObject> checkInstantPayAepsKycStatus(@Header("Authorization") String auth,
                                                  @Field("userID") String userID,
                                                  @Field("tokenKey") String tokenKey,
                                                  @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("InstantPayAepsUserOnboard")
    Call<JsonObject> instantWtsUserOnboard(@Header("Authorization") String auth,
                                           @Field("userID") String userID,
                                           @Field("tokenKey") String tokenKey,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("merchantMobile") String merchantMobile,
                                           @Field("merchantPanNo") String merchantPanNo,
                                           @Field("merchantEmail") String merchantEmail,
                                           @Field("merchantAadhar") String merchantAadhar,
                                           @Field("latitude") String latitude,
                                           @Field("longitude") String longitude,
                                           @Field("consent") String consent);

    @FormUrlEncoded
    @POST("InstantPayAepsUserOnboardVerify")
    Call<JsonObject> instantWtsUserOnboardVerify(@Header("Authorization") String auth,
                                                 @Field("userID") String userID,
                                                 @Field("tokenKey") String tokenKey,
                                                 @Field("deviceInfo") String deviceInfo,
                                                 @Field("otpReferenceID") String otpReferenceID,
                                                 @Field("hash") String hash,
                                                 @Field("otp") String otp,
                                                 @Field("merchantMobile") String merchantMobile);

    @FormUrlEncoded
    @POST("DoInstantPayAepsTransactions")
    Call<JsonObject> doInstantPayAepsTransaction(@Header("Authorization") String auth,
                                                 @Field("userID") String userID,
                                                 @Field("aadharno") String aadharno,
                                                 @Field("bankiinno") String bankiinno,
                                                 @Field("amount") String amount,
                                                 @Field("txntype") String txntype,
                                                 @Field("fingerdata") String fingerdata,
                                                 @Field("latitude") String latitude,
                                                 @Field("longitude") String longitude,
                                                 @Field("mobileno") String mobileno,
                                                 @Field("bankname") String bankname,
                                                 @Field("tokenKey") String tokenKey,
                                                 @Field("deviceInfo") String deviceInfo);
    //////////////////////////////////////////////////////////////////////////

    //  instant pay settlement

    @FormUrlEncoded
    @POST("WPayouts")
    Call<JsonObject> instantPayMoveToBank(@Header("Authorization") String auth,
                                          @Field("userID") String userID,
                                          @Field("tokenKey") String tokenKey,
                                          @Field("deviceInfo") String deviceInfo,
                                          @Field("WalletType") String WalletType,
                                          @Field("Amount") String Amount,
                                          @Field("AccountNo") String AccountNo,
                                          @Field("ifscCode") String ifscCode,
                                          @Field("AccountType") String AccountType,
                                          @Field("TransactionType") String TransactionType,
                                          @Field("AcHolderName") String AcHolderName,
                                          @Field("apiname") String apiname,
                                          @Field("latitude") String latitude,
                                          @Field("longitude") String longitude,
                                          @Field("BankName") String BankName

    );

    @FormUrlEncoded
    @POST("DMTaccountVarify")
    Call<JsonObject> verifyInstantPayAddBankDetails(@Header("Authorization") String auth,
                                                    @Field("userID") String userID,
                                                    @Field("tokenKey") String tokenKey,
                                                    @Field("deviceInfo") String deviceInfo,
                                                    @Field("remitterId") String remitterId,
                                                    @Field("bankName") String bankName,
                                                    @Field("beneFirstName") String beneFirstName,
                                                    @Field("beneLastName") String beneLastName,
                                                    @Field("mobileNo") String mobileNo,
                                                    @Field("ifscCode") String ifscCode,
                                                    @Field("accountNo") String accountNo,
                                                    @Field("senderMobileNo") String senderMobileNo,
                                                    @Field("address") String address,
                                                    @Field("pinCode") String pinCode,
                                                    @Field("otp") String otp,
                                                    @Field("senderName") String senderName,
                                                    @Field("transactionMode") String transactionMode,
                                                    @Field("latitude") String latitude,
                                                    @Field("longitude") String longitude
    );

    ///////////////////////////////////////////////////////////////

    // expressDmt

    @FormUrlEncoded
    @POST("ExpressDmtGetSender")
    Call<JsonObject> isSenderValidateExpress(@Header("Authorization") String auth,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("userID") String userID,
                                             @Field("senderMobileNo") String senderMobileNo);

    @FormUrlEncoded
    @POST("ExpressDmtRemitterRegistration")
    Call<JsonObject> addSenderExpress(@Header("Authorization") String auth,
                                      @Field("tokenKey") String token,
                                      @Field("deviceInfo") String DeviceInfo,
                                      @Field("userID") String userID,
                                      @Field("senderMobileNo") String senderMobileNo,
                                      @Field("Name") String Name,
                                      @Field("pinCode") String pinCode,
                                      @Field("address") String address,
                                      @Field("otp") String otp);

    @FormUrlEncoded
    @POST("ExpressDmtAccountVerification")
    Call<JsonObject> verifyExpressAccount(@Header("Authorization") String auth,
                                          @Field("userID") String userID,
                                          @Field("tokenKey") String tokenKey,
                                          @Field("deviceInfo") String deviceInfo,
                                          @Field("remitterId") String remitterId,
                                          @Field("bankName") String bankName,
                                          @Field("mobileNo") String mobileNo,
                                          @Field("ifscCode") String ifscCode,
                                          @Field("accountNo") String accountNo,
                                          @Field("senderMobileNo") String senderMobileNo,
                                          @Field("senderName") String senderName,
                                          @Field("latitude") String latitude,
                                          @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST("ExpressDmtBeneRegistration")
    Call<JsonObject> addBeneficiaryExpress(@Header("Authorization") String auth,
                                           @Field("userID") String userID,
                                           @Field("tokenKey") String tokenKey,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("remitterId") String remitterId,
                                           @Field("bankName") String bankName,
                                           @Field("beneName") String beneName,
                                           @Field("mobileNo") String mobileNo,
                                           @Field("ifscCode") String ifscCode,
                                           @Field("accountNo") String accountNo,
                                           @Field("senderMobileNo") String senderMobileNo
    );

    @FormUrlEncoded
    @POST("ExpressDmtMoneyTransfer")
    Call<JsonObject> payBeneficiaryExpress(@Header("Authorization") String auth,
                                           @Field("userID") String userID,
                                           @Field("tokenKey") String tokenKey,
                                           @Field("deviceInfo") String deviceInfo,
                                           @Field("txnAmount") String txnAmount,
                                           @Field("beneficiaryId") String beneficiaryId,
                                           @Field("senderName") String senderName,
                                           @Field("senderMobileNo") String senderMobileNo,
                                           @Field("transactionMode") String transactionMode,
                                           @Field("beneMobileNo") String beneMobileNo,
                                           @Field("latitude") String latitude,
                                           @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST("ExpressDmtDeleteBeneficiary")
    Call<JsonObject> deleteBeneficiaryExpress(@Header("Authorization") String auth,
                                              @Field("tokenKey") String tokenKey,
                                              @Field("deviceInfo") String deviceInfo,
                                              @Field("userID") String userID,
                                              @Field("beneid") String beneid);

    @FormUrlEncoded
    @POST("UserExpressDmtReport")
    Call<JsonObject> expressDmtReport(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("fromDate") String fromDate,
                                      @Field("toDate") String toDate,
                                      @Field("Accountno") String Accountno);

    /////////////////////////////////////////////////////////////////////////

    //Check Two Factor Authentication

    @FormUrlEncoded
    @POST("CheckTwofactorAuthentication")
    Call<JsonObject> checkTwoFactorAuthStatus(@Header("Authorization") String auth,
                                              @Field("userID") String userID,
                                              @Field("tokenKey") String tokenKey,
                                              @Field("deviceInfo") String deviceInfo);


    @FormUrlEncoded
    @POST("TwoFactorOnboard")
    Call<JsonObject> twoFactorOnboard(@Header("Authorization") String auth,
                                      @Field("userID") String userID,
                                      @Field("tokenKey") String tokenKey,
                                      @Field("deviceInfo") String deviceInfo,
                                      @Field("aadharNo") String aadharNo,
                                      @Field("mobileNo") String mobileNo,
                                      @Field("fingerData") String fingerData,
                                      @Field("latitude") String latitude,
                                      @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("TwoFAuthentication")
    Call<JsonObject> twoFactorAuthentication(@Header("Authorization") String auth,
                                             @Field("userID") String userID,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("aadharNo") String aadharNo,
                                             @Field("mobileNo") String mobileNo,
                                             @Field("fingerData") String fingerData,
                                             @Field("latitude") String latitude,
                                             @Field("longitude") String longitude
    );

    // Two FA For InstantPay Aeps

    @FormUrlEncoded
    @POST("TwoFAuthentication")
    Call<JsonObject> twoFactorAuthenticationInstantPay(@Header("Authorization") String auth,
                                                       @Field("userID") String userID,
                                                       @Field("tokenKey") String tokenKey,
                                                       @Field("deviceInfo") String deviceInfo,
                                                       @Field("aadharNo") String aadharNo,
                                                       @Field("mobileNo") String mobileNo,
                                                       @Field("fingerData") String fingerData,
                                                       @Field("latitude") String latitude,
                                                       @Field("longitude") String longitude,
                                                       @Field("ApiId") String apiId );

/////////////////////////////////////////////////////////

    // credo aeps

    @FormUrlEncoded
    @POST("CheckCredoAepsOnboard")
    Call<JsonObject> checkCredoserKycStatus(@Header("Authorization") String auth,
                                            @Field("userID") String userID,
                                            @Field("tokenKey") String tokenKey,
                                            @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("CheckCredoDailyTwoFA")
    Call<JsonObject> checkTwoFactorCredoAuthStatus(@Header("Authorization") String auth,
                                                   @Field("userID") String userID,
                                                   @Field("tokenKey") String tokenKey,
                                                   @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("DoCredoAepsTransaction")
    Call<JsonObject> doAepsTransactionCredo(@Header("Authorization") String auth,
                                            @Field("userID") String userID,
                                            @Field("aadharno") String aadharno,
                                            @Field("bankiinno") String bankiinno,
                                            @Field("amount") String amount,
                                            @Field("txntype") String txntype,
                                            @Field("fingerdata") String fingerdata,
                                            @Field("latitude") String latitude,
                                            @Field("longitude") String longitude,
                                            @Field("mobileno") String mobileno,
                                            @Field("tokenKey") String tokenKey,
                                            @Field("deviceInfo") String deviceInfo

    );


    @FormUrlEncoded
    @POST("CredoCWtwoFA")
    Call<JsonObject> twoFactorCWBank(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("fingerData") String fingerData,
                                     @Field("latitude") String latitude,
                                     @Field("longitude") String longitude

    );


    @FormUrlEncoded
    @POST("DoCredoAepsTransaction")
    Call<JsonObject> twoFactorDailyCredo(@Header("Authorization") String auth,
                                         @Field("userID") String userID,
                                         @Field("tokenKey") String tokenKey,
                                         @Field("deviceInfo") String deviceInfo,
                                         @Field("aadharno") String aadharNo,
                                         @Field("mobileno") String mobileNo,
                                         @Field("fingerdata") String fingerData,
                                         @Field("latitude") String latitude,
                                         @Field("longitude") String longitude,
                                         @Field("txntype") String txntype,
                                         @Field("bankiinno") String bankiinno,
                                         @Field("amount") String amount
    );

    @FormUrlEncoded
    @POST("CredoAepsOnboard")
    Call<JsonObject> credoAepsUserOnboard(@Header("Authorization") String auth,
                                          @Field("userID") String userID,
                                          @Field("tokenKey") String tokenKey,
                                          @Field("deviceInfo") String deviceInfo,
                                          @Field("deviceModel") String deviceModel,
                                          @Field("deviceSerialNo") String deviceSerialNo,
                                          @Field("deviceImeiNo") String deviceImeiNo,
                                          @Field("merchantAddress") String merchantAddress,
                                          @Field("merchantPincode") String merchantPincode,
                                          @Field("latitude") String latitude,
                                          @Field("longitude") String longitude

    );


    @FormUrlEncoded
    @POST("DoCredoAepsKyc")
    Call<JsonObject> credoAepsUserOnboardKyc(@Header("Authorization") String auth,
                                             @Field("userID") String userID,
                                             @Field("tokenKey") String tokenKey,
                                             @Field("deviceInfo") String deviceInfo,
                                             @Field("merchantFirstName") String merchantFirstName,
                                             @Field("merchantLastName") String merchantLastName,
                                             @Field("merchantMobile") String merchantMobile,
                                             @Field("merchantDOB") String merchantDOB,
                                             @Field("merchantAddress") String merchantAddress,
                                             @Field("merchantPincode") String merchantPincode,
                                             @Field("merchantPanNo") String merchantPanNo,
                                             @Field("merchantPanUrl") String merchantPanUrl,
                                             @Field("merchantAadhar") String merchantAadhar,
                                             @Field("merchantAadharFrontUrl") String merchantAadharFrontUrl,
                                             @Field("merchantAadharBackUrl") String merchantAadharBackUrl,
                                             @Field("merchantEmail") String merchantEmail,
                                             @Field("merchantCompanyName") String merchantCompanyName,
                                             @Field("merchantAccNo") String merchantAccNo,
                                             @Field("merchantIfscCode") String merchantIfscCode,
                                             @Field("merchantTitle") String merchantTitle,
                                             @Field("merchantCancelChequeUrl") String merchantCancelChequeUrl,
                                             @Field("merchantCancelChequeNo") String merchantCancelChequeNo,
                                             @Field("latitude") String latitude,
                                             @Field("longitude") String longitude);


//////////////////////  credo pay aeps

    @FormUrlEncoded
    @POST("GetParentEarning")
    Call<JsonObject> getTotalEarning(@Header("Authorization") String auth,
                                     @Field("userID") String userID,
                                     @Field("tokenKey") String tokenKey,
                                     @Field("deviceInfo") String deviceInfo,
                                     @Field("fromDate") String fromDate,
                                     @Field("toDate") String toDate
    );


    @FormUrlEncoded
    @POST("UpdateCashfreeAddMoneyWebhook")
    Call<JsonObject> updateAddMoneyPayu(@Header("Authorization") String auth,
                                        @Field("userID") String userID,
                                        @Field("UniqueId") String UniqueId,
                                        @Field("tokenKey") String tokenKey,
                                        @Field("deviceInfo") String deviceInfo,
                                        @Field("Status") String Status,
                                        @Field("TransactionId") String TransactionId
    );

    @FormUrlEncoded
    @POST("GenerateHashForPayUpg")
    Call<JsonObject> generateHash(@Header("Authorization") String auth,
                                  @Field("userID") String userID,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("amount") String amount,
                                  @Field("TransactionId") String transactionId,
                                  @Field("ownerName") String ownerName,
                                  @Field("emailId") String emailId,
                                  @Field("phoneNo") String phoneNo ,
                                  @Field("HashValue") String hashValue

    );
}
