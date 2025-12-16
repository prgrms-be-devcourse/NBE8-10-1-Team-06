import { NextRequest, NextResponse } from "next/server";

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();

    // 요청 데이터 검증
    if (!body.email || !body.address || !body.postalCode || !body.items || body.items.length === 0) {
      return NextResponse.json(
        { message: "필수 필드가 누락되었습니다." },
        { status: 400 }
      );
    }

    // TODO: 실제 백엔드 서버로 주문 데이터 전송
    // const backendResponse = await fetch("http://your-backend-url/api/order", {
    //   method: "POST",
    //   headers: { "Content-Type": "application/json" },
    //   body: JSON.stringify(body),
    // });
    // if (!backendResponse.ok) {
    //   const errorData = await backendResponse.json();
    //   return NextResponse.json(
    //     { message: errorData.message || "주문 처리에 실패했습니다." },
    //     { status: backendResponse.status }
    //   );
    // }

    // 성공 응답 (200 OK) - 빈 객체 반환
    return NextResponse.json({}, { status: 200 });
  } catch (error) {
    console.error("결제 처리 오류:", error);
    return NextResponse.json(
      { message: "서버 오류가 발생했습니다." },
      { status: 500 }
    );
  }
}

