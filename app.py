import streamlit as st
import subprocess
import os
import tempfile

st.title("Huffman Compressor App")

uploaded_file = st.file_uploader("Upload a file to compress", type=["txt", "log", "csv", "json", "xml", "html", "md"])

if uploaded_file is not None:
    with tempfile.TemporaryDirectory() as tmpdirname:
        input_path = os.path.join(tmpdirname, uploaded_file.name)
        with open(input_path, "wb") as f:
            f.write(uploaded_file.getbuffer())

        # Run Java class
        output_path = input_path + ".huff"
        try:
            result = subprocess.run(
                ["java", "MyHuffmanCompressor", input_path],
                capture_output=True,
                text=True,
                check=True
            )
            st.success("File compressed successfully!")
            if os.path.exists(output_path):
                with open(output_path, "rb") as f:
                    st.download_button("Download Compressed File", f, file_name=os.path.basename(output_path))
            else:
                st.error("Output file not found.")
        except subprocess.CalledProcessError as e:
            st.error("Compression failed.")
            st.code(e.stderr or "Unknown error")
