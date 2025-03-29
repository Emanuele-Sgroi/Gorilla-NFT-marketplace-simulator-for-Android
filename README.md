# Gorilla NFT Marketplace Simulator (Android)

> **A university project that simulates an NFT marketplace for Android, featuring real-time crypto conversions, buy/sell flows, and a custom admin panel.**  
> **Showcase Link:** [Gorilla NFT Showcase Website](https://emanuele-sgroi.github.io/showcase-gorilla-nft/)

## Table of Contents
- [Overview](#overview)
- [Core Features](#core-features)
- [Tech Stack](#tech-stack)
- [Installation & Usage](#installation--usage)
- [Why This Project?](#why-this-project)
- [Future Ideas](#future-ideas)
- [App Screenshots](#app-screenshots)
- [License](#license)
- [Contact](#contact)

---

## Overview
Gorilla NFT Marketplace Simulator is an Android app built in Java (Android Studio) that emulates a full NFT platform on mobile devices. Users can:
- Register and log in as standard users or admins.
- List NFTs with a name, price, and preview image.
- Convert prices in real time between Ethereum and USD.
- Buy and sell NFTs within the app, with all data synced to Firebase Realtime Database.

This was originally developed for a university course and ended up going way beyond the typical requirements. Enough to earn a perfect score and be showcased for next-year students.

---

## Core Features

1. **Buy & Sell NFTs**  
   Users can browse and list NFTs. The app updates each item’s name, image, and price in real time.

2. **Real-Time Crypto Conversion**  
   Integrates an external API (CoinLayer) to convert ETH ⇄ USD so users see accurate prices instantly.

3. **Firebase Realtime Database**  
   Stores user profiles, listings, cart data, and transactions. Updates occur on the fly without manual refreshes.

4. **Admin Panel**  
   Admin accounts can moderate listings, remove user-generated content, and respond to inquiries.

5. **User-Friendly UI**  
   Built in Android Studio with Java and XML. The design aims for clarity, including a comfortable, e-commerce-like flow.

---

## Tech Stack

- **Java & XML**: Main codebase for logic and layouts in Android Studio.
- **Firebase Realtime Database**: Keeps data synchronized and updated for all users.
- **CoinLayer API for ETH ⇄ USD Conversions**: Fetches real-time exchange rates to display accurate NFT prices.
- **Admin Panel**: Advanced user roles and privileges built directly into the app.

---

## Installation & Usage

1. **Clone or Download** this repository from GitHub.
2. **Open in Android Studio**.
3. **Add Firebase Credentials**  
   - Create a Firebase project and add your `google-services.json` file to the app’s `app/` directory.
   - Update the `Realtime Database` rules if needed.
4. **Configure API Keys**  
   - If using a public Crypto API (e.g. CoinLayer), add your API key in the relevant Java class or via a gradle property.  
5. **Build and Run** the project on an emulator or Android device.  
   - The app will start with a login screen, then proceed to the main marketplace interface.

---

## Why This Project?

I originally built Gorilla NFT for a mobile development course, and I really wanted to go beyond a standard CRUD assignment. I ended up creating a full NFT marketplace experience, from posting a digital asset to completing a crypto-based transaction. My project ended up being showcased for future students because it demonstrated how an Android app can combine e-commerce flows, real-time data from Firebase, and dynamic crypto conversion, all wrapped in a user-friendly design.

---

## Future Ideas

- **On-Chain Transactions**: Possibly connect to a real testnet or metamask integration for actual blockchain calls.
- **In-App Notifications**: Alert users when new NFTs are listed or if their item sells.
- **Expanded Analytics**: Track user behavior and trending NFTs, both for normal users and admin oversight.
- **Social Features**: Add comments or likes on NFT listings for a more interactive experience.

---

## App Screenshots

![Gorilla NFT Screenshots](./app_screenshots.png)

---

## License

All code is provided for educational reference. If you adapt or build on this project, kindly credit me as the original developer.

---

## Contact

If you have questions:
- **Email**: em.sgroi@gmail.com
- **Portfolio**: [www.emanuelesgroi.com](https://www.emanuelesgroi.com/)

