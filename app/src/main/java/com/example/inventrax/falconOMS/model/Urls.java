package com.example.inventrax.falconOMS.model;

public class Urls {


    private static  String URL_STORE_FRONT_API = "/OMS_StoreFront/api/";
    private static  String URL_MASTERS = "/OMS_Masters/";
    private static  String URL_ORDERS = "/OMS_Orders/";


    // URL's
    public static final String URL_USER_LOGOUT = URL_STORE_FRONT_API + "UserLogOut";
    public static final String URL_FORGOT_PASSWORD = URL_STORE_FRONT_API + "ForgotPassword";
    public static final String URL_PROFILE = URL_STORE_FRONT_API + "Profile";
    public static final String URL_CHANGE_PASSWORD = URL_STORE_FRONT_API + "Changepassword";
    public static final String URL_GET_NOTIFICATIONS = URL_STORE_FRONT_API + "GetNotifications";
    public static final String URL_MAS_PRODUCT_CATALOG = URL_MASTERS + "Master/ProductCatalog";
    public static final String URL_MAS_GET_CUSTOMER_LIST = URL_MASTERS + "Master/GetCustomerList";
    public static final String URL_MAS_GET_CUSTOMER_LIST_MOBILE = URL_MASTERS + "Master/GetCustomerListMobile";
    public static final String URL_MAS_SYNC_ITEM_DATA = URL_MASTERS + "Master/SyncItemData";
    public static final String URL_MAS_SYNC_CUSTOMER_DATA = URL_MASTERS + "Master/SyncCustomerData";
    public static final String URL_ORDER_ASSISTANCE_UPLOAD = URL_STORE_FRONT_API +"OrderAssistanceUpload";
    public static final String URL_ODR_ACTIVE_CART_LIST = URL_ORDERS +"Orders/ActiveCartList";
    public static final String URL_ODR_GET_PRICE = URL_ORDERS + "Orders/GetPrice";
    public static final String URL_MAS_VEHICLE_LIST = URL_MASTERS + "Master/VehicleList";
    public static final String URL_ODR_HHT_CART_DETAILS = URL_ORDERS + "Orders/HHTCartDetails";
    public static final String URL_ODR_PENDING_CART_LIST = URL_ORDERS + "Orders/PendingCartList";
    public static final String URL_ODR_ORDER_FULFILMENT_PROCESS = URL_ORDERS + "Orders/OrderFulfilmentProcess";
    public static final String URL_ODR_DELETE_CART_ITEM = URL_ORDERS + "Orders/DeleteCartItem";
    public static final String URL_ODR_DELETE_CART_ITEM_RESERVATION = URL_ORDERS + "Orders/DeleteCartItemReservation";
    public static final String URL_ODR_DELETE_RESERVATION = URL_ORDERS + "Orders/DeleteReservation";
    public static final String URL_ODR_CANCEL_ORDER = URL_ORDERS + "Orders/CancelOrder";
    public static final String URL_ODR_INITIATE_WORKFLOW = URL_ORDERS + "Orders/InitiateWorkflow";
    public static final String URL_ODR_MM_INTELLI_SEARCH = URL_ORDERS + "Orders/MMIntelliSearch";
    public static final String URL_ODR_INSERT_CREDIT_LIMIT_COMMITMENTS = URL_ORDERS + "Orders/InsertCreditLimitCommitments";
    public static final String URL_ODR_ORDERS_CONFIRMATION = URL_ORDERS + "Orders/OrderConfirmation";
    public static final String URL_ODR_PROCEED_WITH_ORDER_QTY = URL_ORDERS + "Orders/ProceedWithOrderQty";
    public static final String URL_ODR_PROCEED_CART = URL_ORDERS + "Orders/ProcessCart";
    public static final String URL_ODR_SCM_APPROVED_CART_LIST = URL_ORDERS + "Orders/SCMApprovedCartList";
    public static final String URL_ODR_PROCEED_WITH_AVB_QTY = URL_ORDERS + "Orders/ProceedWithAvbQty";
    public static final String URL_ODR_MATERIAL_INTELLI_SEARCH = URL_ORDERS + "Orders/MaterialIntelliSearch";
    public static final String URL_ODR_GET_CART = URL_ORDERS + "Orders/getcart";
    public static final String URL_ODR_APPROVAL_LIST = URL_ORDERS + "Orders/ApprovalList";
    public static final String URL_ODR_APPROVAL_CREDIT_LIMIT_LIST = URL_ORDERS + "Orders/ApprovalCreditLimitList";
    public static final String URL_ODR_APPROVAL_DISCOUNT = URL_ORDERS + "Orders/ApprovalDiscount";
    public static final String URL_ODR_APPROVAL_ITEM_LIST = URL_ORDERS + "Orders/ApprovelItemList";
    public static final String URL_ODR_APPROVAL_LIST_SCMRF = URL_ORDERS + "Orders/ApprovalistSCMRF";
    public static final String URL_ODR_UPSERT_SCMRF_DATA = URL_ORDERS + "Orders/UpsertSCMRFData";
    public static final String URL_ODR_APPROVE_WORK_FLOW = URL_ORDERS + "Orders/ApproveWorkflow";
    public static final String URL_ODR_UPDATE_APPROVAL_CART_LIST = URL_ORDERS + "Orders/UpdateApprovalCartList";
    public static final String URL_MAS_OFFERS = URL_MASTERS + "Master/Offers";
    public static final String URL_ODR_BULK_APPROVE_WORK_FLOW = URL_ORDERS + "Orders/BulkApproveWorkflow";
    public static final String URL_MAS_BULK_APPROVE_WORK_FLOW = URL_MASTERS + "Master/BulkApproveWorkflow";
    public static final String URL_ODR_SO_DEPENDENCIES = URL_ORDERS + "Orders/SODependencies";
    public static final String URL_ODR_MATERIAL_UNDER_DIVISION = URL_ORDERS + "Orders/MaterialUnderDivision";
    public static final String URL_ODR_NHI_ORDER_CREATION = URL_ORDERS + "Orders/NHIOrderCreation";
    public static final String URL_MAS_INCO_TERMS = URL_MASTERS + "Master/Incoterms";
    public static final String URL_MAS_TERM_PAYMENT = URL_MASTERS + "Master/TermPayment";
    public static final String URL_MAS_SHIP_TO_PARTY_CUSTOMER = URL_MASTERS + "Master/ShiptoPartyCustomer";
    public static final String URL_CUSTOMER_UNDER_USER = URL_STORE_FRONT_API+"CustomersunderUser";
    public static final String URL_ODR_DELETE_ITEM_FROM_CART = URL_ORDERS + "Orders/DeleteItemFromCart";
    public static final String URL_ODR_LOG_EXCEPTION = URL_ORDERS + "Orders/LogException";
    public static final String URL_ODR_GET_STOCK = URL_ORDERS + "Orders/GetStock";

}
