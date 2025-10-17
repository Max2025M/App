document.getElementById("enviar").addEventListener("click", async () => {
  const numerosInput = document.getElementById("numeros").value;
  const mensagem = document.getElementById("mensagem").value.trim();
  const statusContainer = document.getElementById("statusContainer");
  if(!mensagem || !numerosInput){alert("Preencha todos os campos!"); return;}

  let numeros = numerosInput.replace(/[^0-9,]/g,"").split(",").flatMap(n=>n.match(/\d{9}/g)||[]);
  statusContainer.innerHTML="";

  let chip = prompt("Usar qual chip? Digite 0 (SIM1) ou 1 (SIM2):","0");
  chip=parseInt(chip)||0;

  for(let num of numeros){
    const card=document.createElement("div");
    card.className="status-card";
    card.innerHTML=`<strong>${num}</strong><span>Enviando...</span>`;
    statusContainer.appendChild(card);

    try{
      await SMS.send({phoneNumber:"+258"+num,message:mensagem,simSlot:chip});
      card.innerHTML=`<strong>${num}</strong><span class="status-success">✅ Enviado com sucesso (SIM ${chip+1})</span><button class="btn-small" onclick="this.parentNode.remove()">Excluir</button>`;
    }catch(e){
      card.innerHTML=`<strong>${num}</strong><span class="status-error">❌ Erro ao enviar</span><button class="btn-small" onclick="this.parentNode.remove()">Excluir</button><button class="btn-small" onclick="reenviar('${num}',${chip},this)">Reenviar</button>`;
    }
  }
});

async function reenviar(numero,chip,btn){
  const card=btn.parentNode;
  card.querySelector(".status-error").textContent="Reenviando...";
  try{
    await SMS.send({phoneNumber:"+258"+numero,message:"Reenvio de teste",simSlot:chip});
    card.innerHTML=`<strong>${numero}</strong><span class="status-success">✅ Reenviado com sucesso</span><button class="btn-small" onclick="this.parentNode.remove()">Excluir</button>`;
  }catch(e){
    card.querySelector(".status-error").textContent="❌ Erro no reenvio";
  }
}
