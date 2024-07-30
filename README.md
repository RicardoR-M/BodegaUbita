# Bodega Ubita Mobile App

## Overview

The Bodega Ubita Mobile App is an innovative solution designed to revolutionize the grocery shopping experience for customers of the 'Ubita' store. This Android application, developed using Kotlin, aims to improve sales efficiency and expand market reach by providing a convenient and user-friendly platform for browsing and purchasing grocery items.

## Screenshots
*Splash and Login Screen*

![image](https://github.com/user-attachments/assets/b62016e0-0688-45a2-ac16-0650dbed2ad6)
![image](https://github.com/user-attachments/assets/c2cf1290-c0d9-4e3e-a604-db357132632b)

*Register and Main Screen*

![image](https://github.com/user-attachments/assets/66836ce5-9e3b-49e7-8737-69bad4d08393)
![image](https://github.com/user-attachments/assets/15ae9ade-c25f-4ebe-90d2-c3a8ff82e10c)

*Product Catalog*

![image](https://github.com/user-attachments/assets/92044225-36df-46fe-b651-a85a3f6d1a37)
![image](https://github.com/user-attachments/assets/232fa9ca-a7a1-4610-9b64-94cb14b20101)

*Shopping Cart*

![image](https://github.com/user-attachments/assets/cbb52b30-cfa0-484a-9222-3924e89c7315)
![image](https://github.com/user-attachments/assets/3c00553f-8405-4372-8ec9-a48f589ac5e4)

*Favorites and success order*

![image](https://github.com/user-attachments/assets/7fa0b7b5-26fd-4397-88fb-d8cd41d8abf7)
![image](https://github.com/user-attachments/assets/58a88d4a-b4c2-496b-8e80-f101b932394b)

*Admin Panels*

![image](https://github.com/user-attachments/assets/1f316c57-0f5d-4e8b-88d3-0fe4856c7018)
![image](https://github.com/user-attachments/assets/0337b49a-6191-41d4-a055-2be969700bcc)
![image](https://github.com/user-attachments/assets/18c88346-3c10-4fc2-9ef8-f32c58685c59)

## Features

- User Authentication: Secure login and registration system.
- Product Catalog: Browse a complete list of available grocery items.
- Category Navigation: Explore products organized by categories.
- Shopping Cart: Add, modify, and remove items from the cart.
- Favorites: Save and manage favorite products for quick access.
- Order Management: Place and track orders.
- Admin Panel: Manage products, categories, and orders (for store administrators).
- Secure Payment Integration: Safe and efficient transaction process.

## Technology Stack

- Language: Kotlin
- Platform: Android
- Backend: Firebase (Authentication, Firestore, Storage)
- Database: Room (for local data persistence)
- UI Components: Android Jetpack
- Networking: Retrofit, Picasso (for image loading)
- Dependency Injection: Not specified in the provided code

## Project Structure

The project follows a standard Android app architecture with the following key components:

- `ui`: Contains all the UI-related code, organized by features (e.g., login, cart, admin).
- `model`: Defines data models used throughout the app.
- `localdb`: Handles local database operations using Room.
- `common`: Includes utility classes and common functions.

## Setup and Installation

1. Clone the repository
2. Open the project in Android Studio
3. Configure Firebase:
   - Create a new Firebase project
   - Add your `google-services.json` file to the app directory
   - Enable Authentication, Firestore, and Storage in your Firebase console
4. Build and run the application on an emulator or physical device

## Usage

### For Customers
- Register or log in to your account
- Browse products by category or use the search function
- Add items to your cart
- Manage your favorites
- Complete the checkout process

### For Administrators
- Log in with admin credentials
- Access the admin panel to manage products, categories, and orders

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).
