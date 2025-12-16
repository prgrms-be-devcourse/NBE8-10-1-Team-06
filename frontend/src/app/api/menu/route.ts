import { NextRequest, NextResponse } from "next/server";

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();

    // 요청 데이터 검증
    if (
      !body.email ||
      !body.category ||
      !body.menu_name ||
      typeof body.price !== "number"
    ) {
      return NextResponse.json(
        { message: "필수 필드가 누락되었습니다." },
        { status: 400 }
      );
    }

    // TODO: 실제 백엔드 서버로 데이터 전송
    // const backendResponse = await fetch("http://your-backend-url/api/menu", {
    //   method: "POST",
    //   headers: { "Content-Type": "application/json" },
    //   body: JSON.stringify(body),
    // });

    // 성공 응답
    return NextResponse.json(
      { message: "생성되었습니다." },
      { status: 200 }
    );
  } catch (error) {
    console.error("상품 추가 제안 오류:", error);
    return NextResponse.json(
      { message: "서버 오류가 발생했습니다." },
      { status: 500 }
    );
  }
}

