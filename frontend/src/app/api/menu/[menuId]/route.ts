import { NextRequest, NextResponse } from "next/server";

export async function DELETE(
  request: NextRequest,
  context: { params: { menuId?: string } }
) {
  try {
    // Next context 및 URL 양쪽에서 menuId 추출 (혹시 context 전달이 안 되는 경우 대비)
    let menuId = context.params?.menuId;
    if (!menuId) {
      const segments = request.nextUrl.pathname.split("/").filter(Boolean);
      menuId = segments[segments.length - 1];
    }

    const body = await request.json().catch(() => ({}));

    if (!menuId) {
      return NextResponse.json(
        { message: "메뉴 ID가 제공되지 않았습니다." },
        { status: 400 }
      );
    }

    if (!body.email) {
      return NextResponse.json(
        { message: "이메일은 필수입니다." },
        { status: 400 }
      );
    }

    const backendResponse = await fetch(
      `http://localhost:8080/api/menu/delete/${menuId}`,
      {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email: body.email }),
      }
    );

    if (!backendResponse.ok) {
      let message = "백엔드 메뉴 삭제에 실패했습니다.";
      try {
        const errorText = await backendResponse.text();
        if (errorText) {
          message = errorText;
        }
      } catch {
        // ignore
      }
      return NextResponse.json(
        { message },
        { status: backendResponse.status }
      );
    }

    let message = "메뉴가 삭제되었습니다.";
    try {
      const text = await backendResponse.text();
      if (text) {
        message = text;
      }
    } catch {
      // ignore
    }

    return NextResponse.json({ message }, { status: 200 });
  } catch (error) {
    console.error("메뉴 삭제 프록시 오류:", error);
    return NextResponse.json(
      { message: "서버 오류가 발생했습니다." },
      { status: 500 }
    );
  }
}

