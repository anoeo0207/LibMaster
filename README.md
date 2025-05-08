# 📚 LIBRO – Ứng dụng Quản lý Thư viện

**LIBRO** là một ứng dụng quản lý thư viện thân thiện, dễ sử dụng, được xây dựng nhằm hỗ trợ nhân viên thư viện và quản trị viên trong việc vận hành hệ thống một cách hiệu quả, trực quan và nhanh chóng. Giao diện được thiết kế hiện đại, phản hồi nhanh, tích hợp chatbot AI hỗ trợ quản lý và tra cứu thông tin.

---

## 🧩 Tính năng chính

### 📖 Quản lý Thư viện (Sách)
- **Thêm sách qua API**: Tìm kiếm và thêm thông tin sách từ Google Books API.
![Giao diện LIBRO](assets/addBookAPI.png "Giao diện thêm sách qua API")

- **Thêm sách thủ công**: Cho phép thêm sách theo cách nhập tay khi API không cung cấp đúng thông tin.
- **Chỉnh sửa sách**: Cập nhật thông tin sách hiện có.
- **Xoá sách**: Xoá sách khỏi hệ thống.

### 📕 Quản lý Mượn sách
- **Tạo yêu cầu mượn sách**: Chọn sách, thành viên và thời gian mượn.
- **Xoá yêu cầu mượn sách**: Hỗ trợ huỷ các yêu cầu sai hoặc không còn hiệu lực.

### 🌐 Quản lý Yêu cầu từ Web (đang phát triển)
- **Chấp nhận/huỷ yêu cầu đăng ký dịch vụ thư viện** từ người dùng web (ví dụ: đăng ký tài khoản mượn sách trực tuyến).
- **Xử lý tự động và lưu trữ hồ sơ yêu cầu** (đang trong quá trình hoàn thiện).

### 📦 Mượn các loại Item khác ngoài sách
- **DVD**
- **Đồ án**
- **Tạp chí**

Mỗi loại item có **form riêng** để nhập thông tin và thống kê tổng số lượng đã mượn/trả theo từng loại.

---

## 🤖 Chatbot AI: Libro

**Libro** là trợ lý AI tích hợp dành riêng cho quản trị viên thư viện.

> **⚠️ Ghi chú quan trọng:**  
> Libro thực chất được xây dựng dựa trên mô hình **Gemma 3 của Ollama**.  
> Mọi bản quyền, công sức phát triển đều thuộc về nhóm tác giả của mô hình **Gemma 3**.  
> Mã nguồn lấy phản hồi từ bot được tham khảo và điều chỉnh từ kênh **How to Create Bot**.

### 📌 Mục đích sử dụng
> *Chatbot Libro không sử dụng hay huấn luyện trên dữ liệu nội bộ của thư viện.*

Libro hỗ trợ quản trị viên với các chức năng sau:
- 🧠 Trả lời câu hỏi liên quan đến nghiệp vụ thư viện (ví dụ: phân loại sách, quy trình xử lý mượn trả…)
- 📄 Viết thô tài liệu nội bộ (quy định, thông báo…)
- 🧾 Gợi ý soạn email nhắc nhở trả sách quá hạn hoặc thư cảm ơn
- 🛠️ Gợi ý truy vấn SQL đơn giản hoặc cách tổ chức database
- 💡 Gợi ý sự kiện thư viện (câu lạc bộ sách, triển lãm, workshop)
- 📊 Hỗ trợ tóm tắt báo cáo theo yêu cầu

> Tốc độ phản hồi trung bình do AI đang chạy trên môi trường **local**, phù hợp cho nhu cầu tư vấn cơ bản.

---

## 🎨 Giao diện
- Thân thiện, dễ sử dụng, tối ưu cho thao tác quản trị viên
- Tốc độ phản hồi giao diện nhanh
- Có hỗ trợ hiển thị bảng, thống kê, biểu mẫu

---

## 📂 Công nghệ sử dụng
- **Java** (JavaFX)
- **MySQL** hoặc tương đương cho hệ quản trị cơ sở dữ liệu
- **FXML** cho xây dựng giao diện
- **Google Books API** cho việc tra cứu sách nhanh
- **Gemma 3 (Ollama)** cho chatbot AI Libro

---

## 📞 Liên hệ & Góp ý
Nếu bạn muốn đóng góp ý tưởng, phát hiện lỗi, hoặc hợp tác phát triển, vui lòng liên hệ qua email dự án.

---

**© 2025 – LIBRO Library Management System**
