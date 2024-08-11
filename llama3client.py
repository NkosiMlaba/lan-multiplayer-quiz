import os
import sys
from dotenv import load_dotenv, dotenv_values

from groq import Groq

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 llama3client.py <prompt>")
        return "invalid command"
    
    if len(sys.argv) > 1:
        load_dotenv()
        client = Groq(
            api_key=os.getenv("GROQ_API_KEY"),
        )

        chat_completion = client.chat.completions.create(
            messages=[
                {
                    "role": "user",
                    "content": f"{sys.argv[1]}",
                }
            ],
            model="llama3-70b-8192",
        )

        print(f"Answer: {chat_completion.choices[0].message.content}")
        return chat_completion.choices[0].message.content

if __name__ == "__main__":
    main()