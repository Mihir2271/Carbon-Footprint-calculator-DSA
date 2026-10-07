// ==========================================
// DATA STRUCTURE 1: HASH MAP (Emission Factors)
// Demonstrates O(1) key-value lookup
// ==========================================
class SimpleHashMap {
  constructor(size = 10) {
    this.buckets = new Array(size);
    this.size = size;
  }

  // Hash function (sum of ASCII codes % table size)
  _hash(key) {
    let hash = 0;
    for (let i = 0; i < key.length; i++) {
      hash += key.charCodeAt(i);
    }
    return hash % this.size;
  }

  // Insert key-value pair
  set(key, value) {
    const index = this._hash(key);
    if (!this.buckets[index]) {
      this.buckets[index] = [];
    }
    for (let item of this.buckets[index]) {
      if (item[0] === key) {
        item[1] = value;
        return;
      }
    }
    this.buckets[index].push([key, value]);
  }

  // Get value by key - O(1) average time complexity
  get(key) {
    const index = this._hash(key);
    const bucket = this.buckets[index];
    if (bucket) {
      for (let item of bucket) {
        if (item[0] === key) return item[1];
      }
    }
    return null;
  }
}

// Initialize and populate Hash Map with reference factors (kg CO2e per unit)
const factorTable = new SimpleHashMap();
factorTable.set("electricity", 0.82); // 0.82 kg CO2 per kWh
factorTable.set("fuel", 2.31);        // 2.31 kg CO2 per Liter
factorTable.set("transport", 0.17);   // 0.17 kg CO2 per km

// ==========================================
// DATA STRUCTURE 2: SINGLY LINKED LIST (Activity Log)
// Demonstrates O(1) head insertion & O(n) traversal
// ==========================================
class LogNode {
  constructor(id, timestamp, electricity, fuel, transport, total) {
    this.id = id;
    this.timestamp = timestamp;
    this.electricity = electricity;
    this.fuel = fuel;
    this.transport = transport;
    this.total = total;
    this.next = null; // Pointer to next node
  }
}

class HistoryLinkedList {
  constructor() {
    this.head = null;
    this.size = 0;
  }

  // Insert at head: O(1)
  insertAtHead(electricity, fuel, transport, total) {
    this.size++;
    const timestamp = new Date().toLocaleTimeString();
    const newNode = new LogNode(this.size, timestamp, electricity, fuel, transport, total);
    newNode.next = this.head;
    this.head = newNode;
  }

  // Traversal to collect entries: O(n)
  getAllEntries() {
    const entries = [];
    let current = this.head;
    while (current !== null) {
      entries.push(current);
      current = current.next;
    }
    return entries;
  }

  // Clear list
  clear() {
    this.head = null;
    this.size = 0;
  }
}

const historyList = new HistoryLinkedList();

// ==========================================
// BUSINESS LOGIC & DOM MANIPULATION
// ==========================================
function calculateFootprint() {
  const eleVal = parseFloat(document.getElementById("electricity").value) || 0;
  const fuelVal = parseFloat(document.getElementById("fuel").value) || 0;
  const transVal = parseFloat(document.getElementById("transport").value) || 0;

  if (eleVal === 0 && fuelVal === 0 && transVal === 0) {
    alert("Please enter at least one value greater than zero.");
    return;
  }

  // O(1) Factor Lookup via Hash Map
  const eleCO2 = eleVal * factorTable.get("electricity");
  const fuelCO2 = fuelVal * factorTable.get("fuel");
  const transCO2 = transVal * factorTable.get("transport");

  const totalCO2 = eleCO2 + fuelCO2 + transCO2;

  // Insert node into Singly Linked List (O(1))
  historyList.insertAtHead(eleVal, fuelVal, transVal, totalCO2.toFixed(2));

  // Determine Primary Hotspot
  const sources = [
    { name: "Electricity Usage", value: eleCO2 },
    { name: "Fuel Consumption", value: fuelCO2 },
    { name: "Daily Transport", value: transCO2 }
  ];
  sources.sort((a, b) => b.value - a.value);

  // Update Output Elements
  document.getElementById("total-output").innerText = 
    `Total Footprint: ${totalCO2.toFixed(2)} kg CO₂e`;
  document.getElementById("hotspot-output").innerText = 
    `Primary Contributor: ${sources[0].name} (${sources[0].value.toFixed(2)} kg CO₂e)`;

  // Re-render the Linked List Table
  renderHistoryTable();
}

function renderHistoryTable() {
  const tbody = document.getElementById("history-body");
  const entries = historyList.getAllEntries();

  if (entries.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; color:#94a3b8;">No entries in linked list yet.</td></tr>`;
    return;
  }

  let html = "";
  for (let entry of entries) {
    html += `
      <tr>
        <td>#${entry.id}</td>
        <td>${entry.timestamp}</td>
        <td>${entry.electricity}</td>
        <td>${entry.fuel}</td>
        <td>${entry.transport}</td>
        <td><strong>${entry.total}</strong></td>
      </tr>
    `;
  }
  tbody.innerHTML = html;
}

function resetForm() {
  document.getElementById("electricity").value = "";
  document.getElementById("fuel").value = "";
  document.getElementById("transport").value = "";
  document.getElementById("electricity").focus();
}

function clearHistory() {
  historyList.clear();
  renderHistoryTable();
  document.getElementById("total-output").innerText = "Total Footprint: -- kg CO₂e";
  document.getElementById("hotspot-output").innerText = "";
}