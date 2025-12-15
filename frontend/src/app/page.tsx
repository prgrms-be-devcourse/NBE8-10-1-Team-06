"use client";

import { useEffect, useMemo, useState } from "react";

type Product = {
  id: string;
  name: string;
  subtitle: string;
  price: number;
};

type CartMap = Record<string, number>;

const currency = new Intl.NumberFormat("ko-KR");
const formatPrice = (value: number) => `${currency.format(value)}원`;

export default function Home() {
  const [products, setProducts] = useState<Product[]>([]);
  const [cart, setCart] = useState<CartMap>({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const res = await fetch("/api/products", { cache: "no-store" });
        if (!res.ok) throw new Error("상품을 불러오지 못했습니다");
        const data: Product[] = await res.json();
        setProducts(data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  const addToCart = (id: string) => {
    setCart((prev) => ({ ...prev, [id]: (prev[id] ?? 0) + 1 }));
  };

  const removeFromCart = (id: string) => {
    setCart((prev) => {
      if (!prev[id]) return prev;
      const next = { ...prev };
      delete next[id];
      return next;
    });
  };

  const totalCount = useMemo(
    () => Object.values(cart).reduce((acc, curr) => acc + curr, 0),
    [cart],
  );

  const totalPrice = useMemo(() => {
    return products.reduce((acc, product) => {
      const qty = cart[product.id] ?? 0;
      return acc + product.price * qty;
    }, 0);
  }, [products, cart]);

  const selectedItems = products.filter((product) => cart[product.id]);

  const handleCheckout = () => {
    alert("주문이 완료되었습니다");
  };

  return (
    <div className="flex min-h-screen items-start justify-center bg-slate-100 py-12">
      <main className="flex w-[1200px] max-w-[95vw] gap-6">
        <section className="flex-1 rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
          <div className="mb-3 flex items-center gap-2">
            <h2 className="text-lg font-semibold text-slate-800">상품 목록</h2>
            <span className="text-xs text-slate-500">임시 더미 데이터</span>
          </div>

          <div className="overflow-hidden rounded-lg border border-slate-200">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-slate-600">
                <tr>
                  <th className="w-16 px-4 py-3">이미지</th>
                  <th className="px-4 py-3">상품명</th>
                  <th className="w-28 px-4 py-3 text-right">가격</th>
                  <th className="w-28 px-4 py-3 text-center">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {loading ? (
                  <tr>
                    <td className="px-4 py-6 text-center" colSpan={4}>
                      상품을 불러오는 중입니다...
                    </td>
                  </tr>
                ) : (
                  products.map((product) => (
                    <tr key={product.id} className="hover:bg-slate-50/80">
                      <td className="px-4 py-3">
                        <div className="h-14 w-14 rounded-md border border-slate-200 bg-gradient-to-br from-slate-100 to-slate-200 shadow-inner" />
                      </td>
                      <td className="px-4 py-3 align-top">
                        <div className="text-sm font-semibold text-slate-800">
                          {product.name}
                        </div>
                        <div className="text-xs text-slate-500">
                          {product.subtitle}
                        </div>
                      </td>
                      <td className="px-4 py-3 text-right font-semibold text-slate-800">
                        {formatPrice(product.price)}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center justify-center gap-2">
                          <button
                            className="rounded-md border border-emerald-600 px-3 py-1 text-xs font-semibold text-emerald-700 transition hover:bg-emerald-600 hover:text-white"
                            onClick={() => addToCart(product.id)}
                          >
                            추가
                          </button>
                          <button
                            className="rounded-md border border-rose-500 px-3 py-1 text-xs font-semibold text-rose-600 transition hover:bg-rose-500 hover:text-white"
                            onClick={() => removeFromCart(product.id)}
                          >
                            삭제
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
                {!loading && products.length === 0 && (
                  <tr>
                    <td className="px-4 py-6 text-center text-slate-500" colSpan={4}>
                      표시할 상품이 없습니다.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>

        <section className="w-[360px] rounded-lg border border-slate-200 bg-slate-100/70 p-4 shadow-sm">
          <h2 className="mb-3 text-base font-semibold text-slate-800">Summary</h2>
          <div className="mb-3 space-y-2 rounded-md bg-white p-3 text-sm shadow-inner">
            {selectedItems.length === 0 ? (
              <p className="text-xs text-slate-500">선택된 상품이 없습니다.</p>
            ) : (
              selectedItems.map((item) => (
                <div key={item.id} className="flex items-center justify-between text-slate-700">
                  <div className="flex items-center gap-2">
                    <span className="h-2 w-2 rounded-full bg-slate-400" />
                    <span>{item.name}</span>
                  </div>
                  <span className="text-xs text-slate-500">x{cart[item.id]}</span>
                </div>
              ))
            )}
          </div>

          <div className="space-y-2 text-sm">
            <label className="block space-y-1">
              <span className="text-slate-600">이메일</span>
              <input
                type="email"
                className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                placeholder="your@email.com"
              />
            </label>
            <label className="block space-y-1">
              <span className="text-slate-600">주소</span>
              <input
                type="text"
                className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                placeholder="주소를 입력하세요"
              />
            </label>
            <label className="block space-y-1">
              <span className="text-slate-600">우편번호</span>
              <input
                type="text"
                className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                placeholder="00000"
              />
            </label>
          </div>

          <p className="mt-4 text-xs text-slate-500">
            당일 오후 2시 이후의 주문은 다음 날 배송을 시작합니다.
          </p>

          <div className="mt-4 flex items-center justify-between rounded-md bg-white px-3 py-2 text-sm shadow-inner">
            <span className="text-slate-600">총 금액</span>
            <div className="text-right">
              <div className="text-lg font-bold text-slate-900">
                {formatPrice(totalPrice)}
              </div>
              <div className="text-xs text-slate-500">
                {totalCount}개 선택됨
              </div>
            </div>
          </div>

          <button
            onClick={handleCheckout}
            className="mt-4 w-full rounded-md bg-slate-900 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-400"
            disabled={selectedItems.length === 0}
          >
            결제하기
          </button>

          <div className="mt-6 rounded-md border border-dashed border-slate-300 bg-white p-3 text-xs text-slate-600">
            서버 REST API 연동 준비: 현재는 /api/products에서 더미 데이터를 받아옵니다.
          </div>
        </section>
      </main>
    </div>
  );
}
