# Multi-Agent Social Feed Simulator with Context-Aware LLM Responses — Android

AI-driven Android simulation of a social media environment where multiple fictional agents generate context-aware responses based on user content, semantic tags, and prior conversational history.

---

## Overview

This project simulates a social media feed in which fictional “friends” react to content using large language model generation. Instead of producing isolated responses, the system keeps track of previous comments and uses that evolving context to shape future outputs.

The project combines prompt construction, lightweight user modeling, semantic tagging, persistent local storage, and Android UI components into a single interactive system.

---

## Key Features

- **Multi-agent response generation**
  - Multiple fictional agents generate comments with different voices and styles

- **Context-aware prompting**
  - Prior comments are included in the prompt so responses evolve over time

- **Semantic tagging pipeline**
  - User posts can be tagged through text, image, and sketch-based input flows

- **Persistent local state**
  - Interaction history is stored locally using a database layer

- **Modular Android architecture**
  - UI rendering, data models, and AI interaction are separated into different components

---

## How It Works

1. The user creates or uploads content
2. The content is converted into semantic tags
3. A set of fictional agents is selected
4. A prompt is built using:
   - agent persona
   - post tags
   - previous comment history
5. The LLM generates new comments
6. Responses are displayed and stored for future interactions

---

## Example Prompt Structure

```text
You are [persona].
React to a social media post whose tags are: [tags].
Use that personality’s voice.
Keep the comment under 20 words.
Previous comments from the group: [history]