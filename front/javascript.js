const best = document.getElementById("quality");

best.addEventListener("change", () =>{
  document.getElementById("quality_value").style.display = best.checked ? "block" : "none";
  document.getElementById("quality_label").style.display = best.checked ? "block" : "none";
});

const best_sound = document.getElementById("quality_sound");

best_sound.addEventListener("change", () => {
  document.getElementById("quality_value_sound").style.display = best_sound.checked ? "block" : "none";
  document.getElementById("quality_label_sound").style.display = best_sound.checked ? "block" : "none";
});

const change_path = document.getElementById("path");

change_path.addEventListener("change", () => {
  document.getElementById("path_value").style.display = change_path.checked ? "block" : "none";
  document.getElementById("path_label").style.display = change_path.checked ? "block" : "none";
});

async function start_download() {
  const quality = document.getElementById("quality_value").value;
  const quality_sound = document.getElementById("quality_value_sound").value;
  const outputEl = document.getElementById("output");

  outputEl.textContent = "";
  outputEl.textContent = "Started dowloading: ";

  const path_value = document.getElementById("path_value").value;
  let data_json = {
    wideo: document.getElementById("wideo").checked,
    quality: null,
    quality_sound: null,
    path: null,
    playlist: document.getElementById("playlist").checked,

    url: document.getElementById("url").value,
  };
  if (best.checked) {
    data_json.quality = quality;
  }else{
    data_json.quality = 0;

  }
  if (best_sound.checked) {
    data_json.quality_sound = quality_sound;
  }else{
    data_json.quality_sound = 0;

  }
  if (change_path.checked){
    data_json.path = path_value;
  }else{
    data_json.path = "0";

  }

  try {
    const res = await fetch(`http://localhost:8080/api/download`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data_json),
    });

    if (!res.ok) {
      const errorText = await res.text();
      console.error("Błąd serwera (status " + res.status + "):", errorText);
      outputEl.textContent = errorText;
      return null;
    }

    // --- czytanie strumienia SSE na bieżąco ---
    const reader = res.body.getReader();
    const decoder = new TextDecoder();

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value, { stream: true });

      const lines = chunk
          .split("\n")
          .filter(line => line.startsWith("data:"))
          .map(line => line.slice(5).trim());

      for (const line of lines) {
        outputEl.textContent += line + "\n";
        const output_div = document.getElementById('output-div');
        output_div.scrollTop = output_div.scrollHeight;
      }
    }
    // --- koniec czytania strumienia ---

    return outputEl.textContent;
  } catch (err) {
    console.error("Błąd pobierania:", err);
    outputEl.textContent += "\n" + err;
    return null;
  }


}

const download = document.getElementById("download");

download.addEventListener("click", () => {
  start_download();
});


async function update_dlp() {
  const outputEl = document.getElementById("output");

  outputEl.textContent = "";
  outputEl.textContent = "Started updating: ";
  try {
    const res = await fetch(`http://localhost:8080/api/update`, {
      method: "POST",
    });

    const data = await res.text();
    document.getElementById("output").textContent = data;

    if (!res.ok) {
      console.error("Błąd serwera (status " + res.status + "):", data);
      return null;
    }

    return data;
  } catch (err) {
    console.error("Błąd pobierania:", err);
    document.getElementById("output").textContent = err;
    return null;
  }

}

const update = document.getElementById("update");

update.addEventListener("click", () => {
  update_dlp();
});

