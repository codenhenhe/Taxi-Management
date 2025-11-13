Hướng dẫn cài đặt backend

1. Tải java 21 về :https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html hoặc tải bản java này: https://adoptium.net/en-GB/temurin/releases?version=21&os=any&arch=any
2. Sau khi tải java 21 về cài đặt biến môi trường

- Vào environment_variable:
  +nhìn vào system variable-> New-> tạo biến JAVA_HOME-> để đường dẫn đến folder JAVA-21 + vd:
  C:\Program Files\Eclipse Adoptium\jdk-21.0.x.x-hotspot
  hoặc C:\Program Files\Java\jdk-21
  -Chọn biến PATH ở system variable và nhấn EDIT:
  +Phương án 1: nếu là dạng liên tục như %MAVEN_HOME%\bin; %JAVA_21%\bin;%CUDA_PATH%\bin; thì ; và chèn vào %JAVA_HOME%\bin; sau đó nhấn OKE
  +Phương án 2: nếu là dạng bảng thì thêm đường dẫn vào %JAVA_HOME%\bin; và nhấn OKE

3. Gõ mở terminal gõ java -version để kiểm tra phiên bản
4. Cài đặt Maven lên apache gõ Maven cài bản 3.9.x
5. Tạo biến môi trường cho Maven:

- system variable-> New-> tạo biến MAVEN_HOME-> để đường dẫn đến folder vd: C:\Program Files\apache-maven-3.9.11
- Chọn biến PATH và nhấn EDIT: tương tự cái trên chèn %MAVEN_HOME% vào

6. Kiểm tra bằng cách gõ mvn --v hoặc mvn -version
7. kiểm tra đường dẫn powershell đã tồn tại chưa trên PATH

- Nếu chưa thì tạo biến POWERSHELl và đường dẫn folder là C:\Windows\System32\WindowsPowerShell\v1.0
- Chèn vào PATH bằng cách chọn EDIT: thêm %POWERSHELL% vào

8. Khởi động lại VSCode
9. vào application.yaml ở src/main/resources/application.yaml kiểm tra lại tên root và mật khẩu
10. vào MySQL workbench hoặc SQLserver check xem đã tạo schema taxi_management chưa? Nếu chưa thì gõ CREATE DATABASE taxi_management
11. Sau đó gõ lệnh .\mvnw spring-boot:run là thành công
