# Movie App

## Tổng quan dự án
- Dự án `Movie App` này là một bài tập lớn về chủ đề xem phim, có triển khai một vài chức năng cơ bản của một ứng dụng xem phim tiêu chuẩn. <br>
- Vì đây là một dự án một người làm nên có thể sẽ không vừa mắt mọi người xem cho lắm, mong mọi người hãy thông cảm vì điều này.

## Tính năng có trong dự án

- 
- 
- 
- 

## Ảnh chụp dự án

![App Screenshot](https://via.placeholder.com/468x300?text=App+Screenshot+Here)
![App Screenshot](https://via.placeholder.com/468x300?text=App+Screenshot+Here)
![App Screenshot](https://via.placeholder.com/468x300?text=App+Screenshot+Here)

## Cấu hình dự án

- Hãy chắc chắn bạn có những thứ sau để cài đặt dự án:
    + **[Java Development Kit](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)** 17
    + **[Android Studio](https://developer.android.com/studio)** phiên bản **`Hedgehog`** (**2023.1.1**) trở lên.
    + **[Git](https://git-scm.com/download/win)** phiên bản bất kì có sẵn
    + Dự án mới trên **[Firebase Console](https://console.firebase.google.com/)**

- Để dự án có thể chạy và không gặp bất cứ lỗi quan trọng nào, <br>bạn sẽ cần đến tệp `private.properties` với nội dung như sau:

    ```properties
    FIREBASE_URL=<YOUR_FIREBASE_URL>
    ```
    thay thế <YOUR_FIREBASE_URL> bằng đường dẫn tham chiếu tới dự án trên Firebase của bạn. Hãy đặt tệp đó vào thư mục gốc của dự án.

- Tiếp tục, bạn sẽ cần đến tệp `google-services.json`, tệp này chỉ có thể lấy được sau khi cấu hình xong dự án trên  **Firebase Console**,
sau khi lấy được nó bạn sẽ đặt vào thư mục `app` của dự án theo đường dẫn phía dưới, **tốt nhất bạn nên thực hiện bước này cuối cùng sau khi bạn đã chắc chắn cấu hình xong dự án Firebase**:
    ```
    <PROJECT_ROOT>\app
    ```
    với **<PROJECT_ROOT>** là vị trí dự án hiện tại của bạn.

- Vì đang sử dụng Firebase làm backend nên dự án ban đầu sẽ không có dữ liệu,<br>
bạn hãy xem video Youtube sau và link GitHub phía dưới để cấu hình dự án và lấy dữ liệu:
    + [Movies app Android Studio Project App Tutorial](https://www.youtube.com/watch?v=yv2e_r8dOf8) (**YouTube**)
    + [worldsat](https://github.com/worldsat/project182) (**GitHub**)

    (Dự án này được base hoàn toàn dựa theo video YouTube trên, xin dược gửi lời cảm ơn tới [@UiLover](https://www.youtube.com/@UiLover))

- Dự án Firebase trên có sử dụng **Firebase `Authentication`**, khi thiết lập phần `Sign-in Method` hãy kích hoạt tính năng xác thực bằng:
    + **Email/Password**
    + **Google**

    riêng với việc xác thực bằng Google thì bạn sẽ phải có **dấu vân tay SHA-1** để Firebase có thể kích hoạt xác thực. <br>
    Để có thể lấy được dấu vân tay đó, bạn cần dùng lệnh sau tại Terminal trong thư mục gốc của dự án:
    ```bash
    ./gradlew signingReport
    ```
    