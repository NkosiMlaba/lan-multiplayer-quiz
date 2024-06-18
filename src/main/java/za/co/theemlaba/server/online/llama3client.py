import os
import sys

from groq import Groq

def main():
    if len(sys.argv) > 1:
        client = Groq(
            api_key="",
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

        print(chat_completion.choices[0].message.content)

if __name__ == "__main__":
    main()