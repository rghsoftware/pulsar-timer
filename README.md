# **Pulsar Timer: An Intelligent Pomodoro Timer for Android & Wear OS**

Pulsar Timer is a smart Pomodoro application designed for anyone who benefits from time-blocking but
struggles with the manual effort of managing timers—especially developers, students, and individuals
with ADHD.

The goal is to create a seamless focus system that integrates directly with your workflow and the
devices you already use, removing the friction that can derail a productive session.

## **The Core Problem**

The Pomodoro Technique is effective, but its success hinges on consistent use. Forgetting to start
the timer, or getting distracted and not restarting it after a break, can break your momentum.
Pulsar Timer eliminates this manual failure point by making the start of a focus session a simple,
tactile, and satisfying ritual.

## **The Architectural Vision**

Pulsar Timer is built as a tightly-integrated system between an Android phone and a Wear OS
smartwatch, using a direct and efficient communication protocol.

The system is composed of two main parts:

1. **The Android App (The Brain):** This is the core of the system. It's a modern Android
   application that runs the main Pomodoro state machine (managing focus, short break, and long
   break sessions). It holds the "single source of truth" for the timer's state.
2. **The Wear OS App (The Remote):** This is a lightweight companion app for your smartwatch. It
   acts as a remote display and controller for the timer running on your phone, allowing you to
   glance at your wrist for status updates and control sessions without touching your phone.

### **The Communication Backbone: Bluetooth LE**

The phone and watch communicate directly using **Bluetooth Low Energy (BLE)**. This creates a
robust, private, and power-efficient link that does not depend on a shared Wi-Fi network or any
external servers.

* **The Android App (GATT Server):** The phone acts as a BLE "server." It advertises a custom "
  Pulsar Timer Service" and makes the current timer status available for the watch to read.
* **The Wear OS App (GATT Client):** The watch acts as a BLE "client." It scans for the phone's
  specific service, connects to it, and subscribes to real-time timer updates. It can also send
  commands (like "pause" or "skip") back to the phone.

## **Key Features**

* **Modern Android App:** A beautiful and functional Pomodoro timer built with 100% Kotlin and
  Jetpack Compose.
* **NFC Tag Integration:** Program inexpensive NFC tags to act as physical triggers. Place a tag on
  your desk, laptop, or notebook. A simple tap of your phone to the tag instantly starts a new focus
  session, creating a powerful ritual to begin work.
* **Wear OS Companion App:**
    * View the current timer status (e.g., FOCUS: 24:59) directly on your wrist.
    * Receive subtle notifications when a session ends.
    * Control the timer from your watch: pause, resume, or skip the current session.

## **Technology Stack**

This project is built using modern, official Android development tools and libraries.

* **Language:** [**Kotlin**](https://kotlinlang.org/)
* **UI Framework:** [**Jetpack Compose**](https://developer.android.com/jetpack/compose) for
  building a declarative, reactive UI on both the phone and the watch.
* **Architecture:** **MVVM** (Model-View-ViewModel) with StateFlow for clean, lifecycle-aware state
  management.
* **Communication:** **Bluetooth Low Energy (BLE)** using a custom GATT Service and Characteristics.
* **Physical Triggers:** **Android NFC API** for reading NDEF-formatted tags.

## **Project Status & Getting Started**

This repository contains the code for the Pulsar Timer Android and Wear OS applications.

### **Development Path:**

1. **Android Core App:** Build the main application with manual Pomodoro controls and the ViewModel
   for state management.
2. **NFC Integration:** Implement the intent-filter and logic to handle NFC tag scans to trigger
   timer sessions.
3. **BLE Server:** Integrate the Bluetooth GATT Server into the Android app to advertise the timer's
   state.
4. **Wear OS Companion:** Build the Wear OS client app to scan for, connect to, and interact with
   the phone's BLE service.
