import openai

client = openai.OpenAI(api_key="")

response = client.chat.completions.create(
    model="gpt-4",
    messages=[{"role": "user", "content": "Me conte uma história interessante!"}],
    stream=True
)

for chunk in response:
    if chunk.choices[0].delta.content:
        print(chunk.choices[0].delta.content, end="", flush=True)