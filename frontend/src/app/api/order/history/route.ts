import { NextRequest, NextResponse } from "next/server";

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const email = body?.email;

    if (!email || typeof email !== "string") {
      return NextResponse.json(
        { message: "이메일은 필수입니다." },
        { status: 400 }
      );
    }

    const backendResponse = await fetch(
      "http://localhost:8080/api/order/list",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email }),
      }
    );

    if (backendResponse.status === 404) {
      return NextResponse.json(
        { message: "해당 이메일의 주문 내역이 없습니다." },
        { status: 404 }
      );
    }

    if (!backendResponse.ok) {
      let message = "주문 내역 조회에 실패했습니다.";
      try {
        const errorBody = await backendResponse.json();
        message =
          errorBody.message || errorBody.msg || errorBody.error || message;
      } catch {
        try {
          const text = await backendResponse.text();
          if (text) message = text;
        } catch {
          // ignore
        }
      }
      return NextResponse.json(
        { message },
        { status: backendResponse.status }
      );
    }

    const data = await backendResponse.json();
    return NextResponse.json(data, { status: 200 });
  } catch (error) {
    console.error("주문 내역 조회 프록시 오류:", error);
    return NextResponse.json(
      { message: "서버 오류가 발생했습니다." },
      { status: 500 }
    );
  }
}

