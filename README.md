# Console-ChatApp-With-Asymmetric-Encryption

This repository contains a simple Java implementation of a secure chat application. The application utilizes cryptographic techniques for key exchange, secure message transmission, and message encryption and decryption capabilities.

## Table of Contents

- [Features](#Features)
- [Dependencies](#Dependencies)
- [Notes](#Notes)
- [Future Improvements](#Future-Improvements)



## Features

- **User Management:** Create new users with unique usernames and associated public/private key pairs.
- **Conversation Establishment:** Initiate new conversations between users with secure key exchange.
- **Message Encryption:** Encrypt message content for secure transmission.
- **Message Decryption:** Decrypt received messages to retrieve the original content.
- **Secure Message Transmission:** Exchange messages securely within established conversations.

## Dependencies

The application relies on Java's cryptography libraries for key generation, key exchange, key derivation, and now, message encryption and decryption. No external libraries or dependencies are needed beyond the standard Java Development Kit (JDK).

## Notes
- The key exchange mechanism utilizes the Diffie-Hellman algorithm.
- Conversations are uniquely identified using a combination of participants' public keys.
- Message content is encrypted using RSA encryption for secure transmission.

## Future Improvements

- **Implement GUI for Better User Experience:** Enhance the user interface by adding a graphical user interface (GUI) for a more intuitive and user-friendly experience.
- **Add Sockets and Threads for Instant Communication:**Integrate socket programming and multithreading to enable instant communication between users. This will improve the real-time nature of the chat application

Happy chatting!
