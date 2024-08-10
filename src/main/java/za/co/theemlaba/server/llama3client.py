import os
import sys
from dotenv import load_dotenv, dotenv_values

from groq import Groq

def main():
    if len(sys.argv) > 1:
        load_dotenv()
        print(os.getenv("GROQ_API_KEY"))
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

        print(chat_completion.choices[0].message.content)

if __name__ == "__main__":
    main()