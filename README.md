![WhatsApp Image 2025-05-20 at 22 53 55_9b2df1ee](https://github.com/user-attachments/assets/7826acdb-644b-47c7-b8d3-88f5cca3e706)
![WhatsApp Image 2025-05-20 at 22 53 56_30c53fd1](https://github.com/user-attachments/assets/04d86629-f84a-480b-979c-b8c87e111271)
![WhatsApp Image 2025-05-20 at 22 53 56_174daf66](https://github.com/user-attachments/assets/f675228c-ab6f-4709-950b-219f7c207ee6)
![WhatsApp Image 2025-05-20 at 22 53 56_10625b79](https://github.com/user-attachments/assets/ce62634e-02a1-45cd-aee7-ade393796277)
![WhatsApp Image 2025-05-20 at 22 53 57_1a99367b](https://github.com/user-attachments/assets/1fd22cf9-51a3-4d17-a9d1-9525907fc1f7)
![WhatsApp Image 2025-05-20 at 22 53 57_9423fea9](https://github.com/user-attachments/assets/0cf41e05-d0ea-47ab-b657-5c035ee85104)
![WhatsApp Image 2025-05-20 at 22 53 58_bd39f296](https://github.com/user-attachments/assets/80811f4d-2c86-4f78-85bc-07b722baef09)
🛋️ VirtuDecor – AR Furniture Shopping App
VirtuDecor is an innovative Augmented Reality (AR) based Furniture Shopping App that allows users to visualize how furniture will look in their home using AR before buying it. The app combines AR technology, Jetpack Compose, Firebase, and Razorpay to deliver a seamless e-commerce experience.

🎯 Key Features
🛒 Browse Furniture
Explore a variety of furniture products with images, price, and descriptions.

🧩 Try in AR (Augmented Reality)
Use your phone’s camera to place 3D models of furniture in your room and preview how it looks in real-time using Sceneform, Google ARCore, and Filament.

💳 Online Payments
Securely place orders using Razorpay Payment Gateway.

🔐 User Authentication
Login and signup using Firebase Authentication (Email & Password).

📦 Order Tracking
View your current and past orders with real-time status updates.

🧰 Tech Stack
Jetpack Compose – Modern Android UI

Firebase – Auth, Firestore, Realtime Database

Google ARCore + Sceneform + Filament – AR functionality and 3D rendering

Razorpay – Secure payments

📁 Folder Structure
VirtuDecor-User/
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/virtudecor/user/
│ │ │ │ ├── ui/ # Jetpack Compose Screens
│ │ │ │ ├── viewmodel/ # ViewModels
│ │ │ │ ├── data/ # Firebase services
│ │ │ │ └── model/ # Data classes
│ │ │ └── res/ # Icons, themes
│ └── build.gradle
├── build.gradle
└── README.md

📝 How to Run the App
Clone the Repository

bash
Copy
Edit
git clone https://github.com/y531ra/User_VirtuDecor.git
cd User_VirtuDecor.
Open in Android Studio
Open the project in Android Studio and sync Gradle.

Add Firebase Configuration

Add google-services.json in the app/ folder

Enable Firebase Authentication (Email/Password)

Create the following Firestore/Realtime Database collections:

/furniture/ – for storing product details

/orders/ – for user order tracking

Upload AR Models
Ensure .glb files are uploaded (via the admin app) and associated with furniture items.

Enable Razorpay Payments

Add your Razorpay API key in the app’s secure config or backend

💡 AR Notes
Make sure your device supports ARCore.

3D models must be in .glb format and properly optimized for mobile use.

🖼️ Screenshots (Optional)
Home Screen	AR View	Checkout
		

🔐 Authentication
Uses Firebase Email & Password authentication. New users can register and existing users can log in to track their orders.

📃 License
This project is licensed under the MIT License.

🙋‍♂️ Author
raghav
Android Developer | BTech Graduate | Passionate about AR and Android
LinkedIn https://www.linkedin.com/in/raghav-anand531/


