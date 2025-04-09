# 💼 UPI Wallet System – Java Console Application

---

## ❓ What is this project?

This is a Java-based **Digital Wallet System** built using **pure Object-Oriented Programming (OOP)** principles. It simulates the core functionality of real-world UPI applications like **PhonePe** or **Google Pay**, but at a smaller and educational scale. The focus of this project is to help demonstrate practical use of **Encapsulation, Inheritance, Abstraction, Polymorphism**, and other OOP concepts.

This project is especially suitable for:
- **Machine Coding Rounds** in interviews
- **LLD (Low-Level Design) practice**
- Academic assignments

All logic runs via the **Java console (Terminal)** using simple menu-driven interactions. No GUI or database is used.

---

## ✅ Functional Requirements (Implemented Features)

### 1. **Create Wallet (Register User)**
- The user can register by entering their name and details
- A **bank account and UPI account** are created internally
- User is assigned a unique UPI ID

### 2. **Transfer Amount**
- Users can transfer money using UPI ID
- Money is transferred from **sender’s bank account** to **receiver’s bank account**
- Proper validations ensure:
  - **Sender has sufficient balance**
  - **Minimum transfer is ₹0.01**

### 3. **Request Money**
- A user can request money from another user
- Receiver can accept or reject the request

### 4. **Account Statement (Transaction History)**
- User can view complete transaction history:
  - Transaction ID
  - Sender / Receiver
  - Amount
  - Timestamp
  - Status (Success / Rejected)
- Saved using **file handling** (not database)

### 5. **Balance Enquiry**
- User can check current balance in their bank account

### 6. **Recharge and Bill Payment**
- Electricity Bill
- Mobile Recharge
- FASTag Recharge

### 7. **International Transfer (Optional / Future Ready)**
- Class structure is prepared for sending money internationally
- Supports currency conversion logic
- Not fully implemented, but designed for scalability

### 8. **Exit Option**
- Exits the console-based program safely

---

## 🧩 Optional Features (Future-Ready)

- **Offers / Cashback (Future Scope)**
  - Example Offer: Get 10% cashback on transferring ₹500+
  - Example Offer: If both users' balances match, sender gets 5% cashback
- **Login system with PIN (for security)**
- **QR Code generation and scanning**

---

## 🛠️ Technologies & Design Patterns

- **Language**: Java (JDK 18)
- **IDE**: Eclipse IDE 2024-09
- **Design Patterns Used**:
  - **Singleton** – For `UPIRegistry` class (central record keeper)
  - **Factory Pattern** – For creating transaction/request objects

---

## 🔁 Project Flow Overview

```
User Input via Console
        ↓
MainApp.java (Driver File)
        ↓
UPIPaymentServiceImpl (Implements PaymentService)
        ↓
Handles Transaction Logic, File Save, Exception Handling
        ↓
Transaction history saved in File
```

---

## 📦 Folder Structure

```
UPI_Wallet_Project/
├── Diagram/
│   └── class_diagram.png
├── Code/
│   └── (All .java files in packages)
├── Software_Details/
│   └── software_info.txt
├── Read_Me/
│   └── README.md
├── Word_Document/
│   └── UPI_Project_Report.docx
```

---

## 📌 Summary

This project provides a fully functional, console-based UPI wallet system using core Java. It demonstrates complete understanding of OOP, exception handling, file handling, and simple LLD. It’s scalable, easy to extend, and ideal for showcasing Java skills in interviews or academic presentations.

Let me know if you'd like this exported as a `README.md`, `.txt`, or `.docx` format!

