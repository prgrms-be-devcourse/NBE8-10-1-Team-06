import { NextResponse } from "next/server";

const products = [
  {
    id: "1",
    name: "커피콩",
    subtitle: "Columbia Narino",
    price: 15860,
  },
  {
    id: "2",
    name: "커피콩",
    subtitle: "Brazil Serra Do Caparao",
    price: 10000,
  },
  {
    id: "3",
    name: "커피콩",
    subtitle: "Columbia Quintio (Wine Extended Fermentation)",
    price: 10420,
  },
  {
    id: "4",
    name: "커피콩",
    subtitle: "Ethiopia Sidamo",
    price: 10000,
  },
];

export async function GET() {
  return NextResponse.json(products);
}

