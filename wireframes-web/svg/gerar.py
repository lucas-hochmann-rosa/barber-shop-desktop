# -*- coding: utf-8 -*-
"""Wireframes de baixa fidelidade — BarberDesk Web (Etapa 8)."""
import os

W = 1440
STROKE = "#8E959C"
BOX = "#F4F5F6"
IMG = "#E3E6E8"
LINE = "#C6CBD0"
INK = "#33383D"
MUT = "#6B7278"
FONT = "DejaVu Sans, Arial, sans-serif"

parts = []
def add(s): parts.append(s)

def rect(x, y, w, h, fill=BOX, stroke=STROKE, rx=4, sw=1.5, dash=None):
    d = ' stroke-dasharray="%s"' % dash if dash else ""
    st = ' stroke="%s" stroke-width="%g"' % (stroke, sw) if stroke else ""
    add('<rect x="%g" y="%g" width="%g" height="%g" rx="%g" fill="%s"%s%s/>' % (x, y, w, h, rx, fill, st, d))

def txt(x, y, s, size=14, weight="normal", fill=INK, anchor="start", sp=0):
    add('<text x="%g" y="%g" font-family="%s" font-size="%g" font-weight="%s" fill="%s" text-anchor="%s" letter-spacing="%g">%s</text>'
        % (x, y, FONT, size, weight, fill, anchor, sp, s))

def bar(x, y, w, h=9, fill=LINE):
    add('<rect x="%g" y="%g" width="%g" height="%g" rx="%g" fill="%s"/>' % (x, y, w, h, h/2.0, fill))

def para(x, y, w, n=3, gap=17, h=8, last=0.6):
    for i in range(n):
        bar(x, y + i*gap, w*(last if i == n-1 else 1.0), h)

def imgph(x, y, w, h, label="Imagem"):
    rect(x, y, w, h, fill=IMG)
    add('<path d="M%g %g L%g %g M%g %g L%g %g" stroke="%s" stroke-width="1.2" opacity="0.7"/>' % (x,y,x+w,y+h,x+w,y,x,y+h,STROKE))
    txt(x+w/2, y+h/2+4, label, 12, "normal", MUT, "middle")

def botao(x, y, w, h, label, cheio=True):
    rect(x, y, w, h, fill="#DCDFE2" if cheio else "#FFFFFF", rx=h/2.0, sw=1.6)
    txt(x+w/2, y+h/2+5, label, 13.5, "bold" if cheio else "normal", INK, "middle")

def campo(x, y, w, label, h=42, tipo="text"):
    txt(x, y-8, label, 12.5, "bold", MUT)
    rect(x, y, w, h, fill="#FFFFFF")
    if tipo == "select":
        add('<path d="M%g %g L%g %g L%g %g Z" fill="%s"/>' % (x+w-28, y+h/2-3, x+w-16, y+h/2-3, x+w-22, y+h/2+5, MUT))
        bar(x+13, y+h/2-4, min(120, w-60), 8)
    elif tipo == "data":
        rect(x+w-36, y+9, 22, 22, fill=BOX, rx=3, sw=1.2)
        bar(x+13, y+h/2-4, min(100, w-60), 8)
    else:
        bar(x+13, y+h/2-4, min(140, w-40), 8)
    return y + h

def nota(x, y, s):
    txt(x, y, "\u25b8 " + s, 12, "normal", "#7E8790")

def moldura(titulo, sub, altura):
    top = 92
    total = altura + top
    add('<svg xmlns="http://www.w3.org/2000/svg" width="%g" height="%g" viewBox="0 0 %g %g">' % (W, total, W, total))
    add('<rect width="%g" height="%g" fill="#FFFFFF"/>' % (W, total))
    rect(0, 0, W, top, fill="#F7F8F9", stroke=None, rx=0, sw=0)
    txt(60, 40, titulo, 21, "bold", INK, "start", 0.6)
    txt(60, 65, sub, 13, "normal", MUT)
    txt(W-60, 40, "WIREFRAME \u00b7 BARBERDESK WEB", 12, "bold", MUT, "end", 1)
    txt(W-60, 65, "Baixa fidelidade \u00b7 1440px", 11.5, "normal", MUT, "end")
    add('<line x1="0" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="2"/>' % (top, W, top, STROKE))
    return top

def salvar(nome):
    add("</svg>")
    open(nome, "w", encoding="utf-8").write("\n".join(parts))
    del parts[:]
    print("gerado:", nome)

SB = 230  # largura da barra lateral

def barra_lateral(y0, altura, ativo="Agenda"):
    rect(0, y0, SB, altura, fill="#EDEFF1", stroke=None, rx=0, sw=0)
    add('<line x1="%g" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="1.5"/>' % (SB, y0, SB, y0+altura, STROKE))
    rect(24, y0+26, 44, 44, fill=IMG, rx=8)
    txt(46, y0+54, "LOGO", 10, "bold", MUT, "middle", .5)
    txt(82, y0+46, "BarberDesk", 15, "bold", INK)
    txt(82, y0+63, "Barbearia do Lucas", 11, "normal", MUT)
    itens = ["Agenda", "Novo agendamento", "Minha barbearia", "Hist\u00f3rico", "Relat\u00f3rios"]
    y = y0 + 106
    for it in itens:
        if it == ativo:
            rect(14, y, SB-28, 40, fill="#FFFFFF", rx=8, sw=1.4)
            add('<rect x="14" y="%g" width="4" height="40" rx="2" fill="%s"/>' % (y, INK))
        rect(28, y+12, 16, 16, fill=IMG, rx=3, sw=1.2)
        txt(56, y+26, it, 13.5, "bold" if it == ativo else "normal", INK if it == ativo else MUT)
        y += 46
    add('<line x1="14" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="1"/>' % (y+8, SB-14, y+8, STROKE))
    txt(28, y+34, "Sair", 13, "normal", MUT)
    nota(28, y0+altura-24, "Menu recolh\u00edvel em telas pequenas")

def barra_topo(y0, titulo, largura=W-SB, x0=SB):
    rect(x0, y0, largura, 78, fill="#FFFFFF", stroke=None, rx=0, sw=0)
    add('<line x1="%g" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="1.5"/>' % (x0, y0+78, x0+largura, y0+78, STROKE))
    txt(x0+36, y0+48, titulo, 19, "bold", INK)
    rect(x0+largura-230, y0+20, 130, 38, fill=BOX, rx=19, sw=1.2)
    txt(x0+largura-165, y0+44, "13/08/2026", 12.5, "normal", MUT, "middle")
    add('<circle cx="%g" cy="%g" r="19" fill="%s" stroke="%s" stroke-width="1.4"/>' % (x0+largura-56, y0+39, IMG, STROKE))
    txt(x0+largura-56, y0+44, "LR", 12, "bold", MUT, "middle")
    return y0 + 78


# ---------------------------------------------------------------- 1. LOGIN
def login():
    y = moldura("01 \u2014 Entrar", "Autentica\u00e7\u00e3o do usu\u00e1rio (RF02)", 760)
    rect(0, y, W, 760, fill="#F7F8F9", stroke=None, rx=0, sw=0)
    # painel ilustrativo
    imgph(0, y, 640, 760, "Imagem da barbearia")
    cx = 640 + (W-640)/2
    rect(cx-210, y+150, 420, 460, fill="#FFFFFF", rx=14)
    rect(cx-32, y+186, 64, 64, fill=IMG, rx=12)
    txt(cx, y+224, "LOGO", 11, "bold", MUT, "middle", .5)
    txt(cx, y+290, "Entrar", 24, "bold", INK, "middle")
    txt(cx, y+316, "Acesse o painel da sua barbearia", 12.5, "normal", MUT, "middle")
    campo(cx-160, y+356, 320, "Usu\u00e1rio")
    campo(cx-160, y+436, 320, "Senha")
    botao(cx-160, y+506, 320, 46, "Entrar")
    txt(cx, y+580, "Primeiro acesso? Cadastre sua barbearia", 12, "normal", MUT, "middle")
    nota(700, y+700, "Valida\u00e7\u00e3o em JavaScript: campos obrigat\u00f3rios e mensagem de erro sem recarregar a p\u00e1gina")
    salvar("01-login.svg")

# ---------------------------------------------------------------- 2. AGENDA
def agenda():
    ALT = 1180
    y0 = moldura("02 \u2014 Agenda do dia", "Tela principal: r\u00e9gua do dia, servi\u00e7os e agendamentos pendentes (RF08, RF11)", ALT)
    barra_lateral(y0, ALT, "Agenda")
    y = barra_topo(y0, "Agenda do dia")
    x = SB + 36
    cw = W - SB - 72

    # régua do dia (elemento-assinatura)
    y += 34
    rect(x, y, cw, 150, fill="#FFFFFF")
    txt(x+22, y+34, "R\u00c9GUA DO DIA", 13, "bold", INK, "start", 1)
    txt(x+cw-22, y+34, "agora \u00b7 14:20", 12, "normal", MUT, "end")
    # trilho
    tx, tw = x+22, cw-44
    add('<rect x="%g" y="%g" width="%g" height="10" rx="5" fill="%s"/>' % (tx, y+78, tw, LINE))
    for i, frac in enumerate([0.05, 0.22, 0.38, 0.52, 0.71, 0.88]):
        bx = tx + tw*frac
        rect(bx, y+62, 54, 42, fill=IMG, rx=6, sw=1.3)
    # marcador de agora
    add('<line x1="%g" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="2.5"/>' % (tx+tw*0.46, y+52, tx+tw*0.46, y+118, INK))
    add('<circle cx="%g" cy="%g" r="6" fill="%s"/>' % (tx+tw*0.46, y+52, INK))
    for i, h in enumerate(["08h", "10h", "12h", "14h", "16h", "18h", "20h"]):
        txt(tx + tw*i/6.0, y+134, h, 11, "normal", MUT, "middle")
    y += 150
    nota(x, y+24, "Elemento-assinatura: posi\u00e7\u00e3o de cada atendimento no dia e marcador da hora atual")

    # cartões-resumo
    y += 46
    gap = 20
    kw = (cw - 3*gap)/4.0
    rotulos = [("Hoje", "8 atendimentos"), ("Atrasados", "1"), ("Em atendimento", "1"), ("Faturamento previsto", "R$ 000,00")]
    for i, (a, b) in enumerate(rotulos):
        kx = x + i*(kw+gap)
        rect(kx, y, kw, 92, fill="#FFFFFF")
        txt(kx+18, y+32, a, 11.5, "bold", MUT, "start", .8)
        txt(kx+18, y+66, b, 17, "bold", INK)
    y += 92

    # serviços
    y += 44
    txt(x, y, "Servi\u00e7os oferecidos", 16, "bold", INK)
    y += 20
    sw_ = (cw - 3*gap)/4.0
    for i in range(4):
        sx = x + i*(sw_+gap)
        rect(sx, y, sw_, 150, fill="#FFFFFF")
        imgph(sx+14, y+14, sw_-28, 74, "Foto")
        bar(sx+14, y+102, sw_*0.55, 10)
        bar(sx+14, y+122, sw_*0.32, 8)
    y += 150
    nota(x, y+22, "Grid de servi\u00e7os com foto, nome e pre\u00e7o (RF03)")

    # tabela de agendamentos
    y += 82
    add('<text x="%g" y="%g" font-family="%s" font-size="16" font-weight="bold" fill="%s">Agendamentos pendentes</text>' % (x, y, FONT, INK))
    botao(x+cw-190, y-24, 190, 40, "+ Novo agendamento")
    y += 20
    rect(x, y, cw, 300, fill="#FFFFFF")
    # cabeçalho
    rect(x, y, cw, 44, fill=BOX, rx=0)
    cols = ["", "Hora", "Cliente", "Servi\u00e7o", "Barbeiro", "Status", "A\u00e7\u00f5es"]
    pos = [0, 0.04, 0.14, 0.34, 0.52, 0.68, 0.85]
    for c, pfrac in zip(cols, pos):
        if c:
            txt(x+18+cw*pfrac, y+28, c, 12, "bold", MUT, "start", .5)
    for r in range(5):
        ry = y + 44 + r*51
        add('<line x1="%g" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="1"/>' % (x, ry, x+cw, ry, "#E8EBED"))
        # faixa de status (assinatura)
        add('<rect x="%g" y="%g" width="6" height="34" rx="3" fill="%s"/>' % (x+12, ry+9, MUT if r % 2 else LINE))
        bar(x+18+cw*0.04, ry+21, 46, 10)
        bar(x+18+cw*0.14, ry+21, cw*0.14, 9)
        bar(x+18+cw*0.34, ry+21, cw*0.12, 9)
        bar(x+18+cw*0.52, ry+21, cw*0.10, 9)
        rect(x+18+cw*0.68, ry+13, 92, 26, fill=BOX, rx=13, sw=1.2)
        for b in range(3):
            rect(x+18+cw*0.85 + b*34, ry+13, 28, 26, fill="#FFFFFF", rx=6, sw=1.2)
    y += 300
    nota(x, y+24, "Faixa colorida \u00e0 esquerda indica a classifica\u00e7\u00e3o do agendamento (RF11)")
    nota(x, y+46, "A\u00e7\u00f5es por linha: iniciar, concluir e editar (RF06, RF07)")
    salvar("02-agenda.svg")

# ---------------------------------------------------------- 3. AGENDAMENTO
def agendamento():
    ALT = 900
    y0 = moldura("03 \u2014 Novo agendamento", "Formul\u00e1rio com valida\u00e7\u00e3o em JavaScript (RF05, RF10)", ALT)
    barra_lateral(y0, ALT, "Novo agendamento")
    y = barra_topo(y0, "Novo agendamento")
    x = SB + 36
    cw = W - SB - 72

    y += 34
    rect(x, y, cw*0.62, 660, fill="#FFFFFF")
    fx = x + 34
    fw = cw*0.62 - 68
    meia = (fw - 24)/2.0
    yy = y + 60
    campo(fx, yy, fw, "Nome do cliente *")
    yy += 78
    campo(fx, yy, meia, "Contato *")
    campo(fx+meia+24, yy, meia, "Origem do contato", tipo="select")
    yy += 78
    campo(fx, yy, meia, "Data *", tipo="data")
    campo(fx+meia+24, yy, meia, "Hora *")
    yy += 78
    campo(fx, yy, meia, "Servi\u00e7o *", tipo="select")
    campo(fx+meia+24, yy, meia, "Barbeiro *", tipo="select")
    yy += 78
    txt(fx, yy-8, "Observa\u00e7\u00f5es", 12.5, "bold", MUT)
    rect(fx, yy, fw, 110, fill="#FFFFFF")
    yy += 110 + 40
    botao(fx, yy, 170, 46, "Salvar")
    botao(fx+186, yy, 130, 46, "Cancelar", cheio=False)

    # painel lateral: aviso de conflito
    px = x + cw*0.62 + 24
    pw = cw*0.38 - 24
    rect(px, y, pw, 200, fill="#FFFFFF", dash="6 5")
    txt(px+22, y+38, "Verifica\u00e7\u00e3o de conflito", 14.5, "bold", INK)
    para(px+22, y+62, pw-44, 3, 18, 8)
    rect(px+22, y+126, pw-44, 50, fill=BOX, rx=8, sw=1.3)
    txt(px+38, y+156, "\u26a0  Hor\u00e1rio ocupado para este barbeiro", 12, "bold", MUT)
    nota(px, y+230, "Mensagem exibida sem recarregar a p\u00e1gina")

    rect(px, y+270, pw, 200, fill="#FFFFFF")
    txt(px+22, y+308, "Resumo", 14.5, "bold", INK)
    for i in range(3):
        bar(px+22, y+336 + i*26, pw-80, 9)
    add('<line x1="%g" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="1"/>' % (px+22, y+424, px+pw-22, y+424, LINE))
    txt(px+22, y+452, "Total", 13, "bold", MUT)
    txt(px+pw-22, y+452, "R$ 00,00", 15, "bold", INK, "end")
    salvar("03-agendamento.svg")

# ---------------------------------------------------------- 4. MINHA BARBEARIA
def barbearia():
    ALT = 940
    y0 = moldura("04 \u2014 Minha barbearia", "Dados da barbearia, servi\u00e7os e barbeiros (RF03, RF04)", ALT)
    barra_lateral(y0, ALT, "Minha barbearia")
    y = barra_topo(y0, "Minha barbearia")
    x = SB + 36
    cw = W - SB - 72

    # abas
    y += 30
    abas = ["Dados", "Servi\u00e7os", "Barbeiros"]
    ax = x
    for i, a in enumerate(abas):
        aw = 130
        rect(ax, y, aw, 44, fill="#FFFFFF" if i == 1 else BOX, rx=8, sw=1.4)
        txt(ax+aw/2, y+28, a, 13.5, "bold" if i == 1 else "normal", INK if i == 1 else MUT, "middle")
        ax += aw + 10
    y += 44
    add('<line x1="%g" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="1.5"/>' % (x, y, x+cw, y, STROKE))

    y += 34
    txt(x, y, "Servi\u00e7os cadastrados", 16, "bold", INK)
    botao(x+cw-150, y-24, 150, 40, "+ Novo servi\u00e7o")
    y += 24
    gap = 20
    kw = (cw - 3*gap)/4.0
    for r in range(2):
        for i in range(4):
            kx = x + i*(kw+gap)
            ky = y + r*(210+gap)
            rect(kx, ky, kw, 210, fill="#FFFFFF")
            imgph(kx+14, ky+14, kw-28, 96, "Foto")
            bar(kx+14, ky+126, kw*0.6, 11)
            bar(kx+14, ky+148, kw*0.35, 9)
            rect(kx+14, ky+170, 60, 26, fill=BOX, rx=6, sw=1.2)
            rect(kx+80, ky+170, 60, 26, fill=BOX, rx=6, sw=1.2)
    y += 210*2 + gap
    nota(x, y+26, "Cada cart\u00e3o traz editar e excluir; formul\u00e1rio abre em janela modal")
    salvar("04-minha-barbearia.svg")

# ---------------------------------------------------------------- 5. HISTÓRICO
def historico():
    ALT = 860
    y0 = moldura("05 \u2014 Hist\u00f3rico", "Listagem completa com filtros (RF09)", ALT)
    barra_lateral(y0, ALT, "Hist\u00f3rico")
    y = barra_topo(y0, "Hist\u00f3rico de agendamentos")
    x = SB + 36
    cw = W - SB - 72

    # filtros
    y += 30
    rect(x, y, cw, 110, fill="#FFFFFF")
    fx = x + 24
    campo(fx, y+40, 200, "De", tipo="data")
    campo(fx+220, y+40, 200, "At\u00e9", tipo="data")
    campo(fx+440, y+40, 200, "Barbeiro", tipo="select")
    campo(fx+660, y+40, 200, "Status", tipo="select")
    botao(x+cw-150, y+40, 126, 42, "Filtrar")
    y += 110
    nota(x, y+24, "Filtros aplicados em JavaScript sobre os dados j\u00e1 carregados")

    # tabela
    y += 46
    rect(x, y, cw, 430, fill="#FFFFFF")
    rect(x, y, cw, 44, fill=BOX, rx=0)
    cols = ["Data", "Hora", "Cliente", "Servi\u00e7o", "Barbeiro", "Status", "Valor"]
    pos = [0.02, 0.12, 0.20, 0.40, 0.56, 0.72, 0.88]
    for c, pfrac in zip(cols, pos):
        txt(x+18+cw*pfrac, y+28, c, 12, "bold", MUT, "start", .5)
    for r in range(7):
        ry = y + 44 + r*55
        add('<line x1="%g" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="1"/>' % (x, ry, x+cw, ry, "#E8EBED"))
        for c, pfrac in zip(cols, pos):
            wbar = 52 if c in ("Data", "Hora", "Valor") else cw*0.11
            bar(x+18+cw*pfrac, ry+23, wbar, 9)
    y += 430
    nota(x, y+24, "Ordena\u00e7\u00e3o por coluna e pagina\u00e7\u00e3o no rodap\u00e9 da tabela")
    salvar("05-historico.svg")

# ---------------------------------------------------------------- 6. RELATÓRIOS
def relatorios():
    ALT = 900
    y0 = moldura("06 \u2014 Relat\u00f3rios", "Faturamento, servi\u00e7os mais vendidos e ranking (RF09)", ALT)
    barra_lateral(y0, ALT, "Relat\u00f3rios")
    y = barra_topo(y0, "Relat\u00f3rios")
    x = SB + 36
    cw = W - SB - 72

    y += 30
    rect(x, y, cw, 96, fill="#FFFFFF")
    campo(x+24, y+34, 200, "Per\u00edodo de", tipo="data")
    campo(x+244, y+34, 200, "at\u00e9", tipo="data")
    botao(x+cw-150, y+34, 126, 42, "Gerar")
    y += 96

    # faturamento
    y += 36
    rect(x, y, cw*0.46, 300, fill="#FFFFFF")
    txt(x+24, y+40, "Faturamento no per\u00edodo", 15, "bold", INK)
    txt(x+24, y+92, "R$ 0.000,00", 30, "bold", INK)
    bx, bw2 = x+24, cw*0.46-48
    for i in range(6):
        hgt = [40, 66, 52, 88, 74, 100][i]
        cwid = (bw2 - 5*14)/6.0
        add('<rect x="%g" y="%g" width="%g" height="%g" rx="4" fill="%s"/>' % (bx + i*(cwid+14), y+250-hgt, cwid, hgt, LINE))
    add('<line x1="%g" y1="%g" x2="%g" y2="%g" stroke="%s" stroke-width="1.4"/>' % (bx, y+252, bx+bw2, y+252, STROKE))
    txt(x+24, y+278, "Gr\u00e1fico de barras em CSS puro", 11.5, "normal", MUT)

    # serviços mais vendidos
    px = x + cw*0.46 + 24
    pw = cw*0.54 - 24
    rect(px, y, pw, 300, fill="#FFFFFF")
    txt(px+24, y+40, "Servi\u00e7os mais vendidos", 15, "bold", INK)
    for i in range(5):
        ry = y + 74 + i*44
        bar(px+24, ry, pw*0.22, 10)
        add('<rect x="%g" y="%g" width="%g" height="14" rx="7" fill="%s"/>' % (px+24+pw*0.26, ry-3, (pw*0.52)*[1, .8, .62, .45, .3][i], LINE))
        txt(px+pw-24, ry+8, "00", 12, "bold", MUT, "end")
    y += 300

    # ranking
    y += 30
    rect(x, y, cw, 260, fill="#FFFFFF")
    txt(x+24, y+40, "Ranking de barbeiros", 15, "bold", INK)
    gap = 20
    kw = (cw - 48 - 2*gap)/3.0
    for i in range(3):
        kx = x + 24 + i*(kw+gap)
        rect(kx, y+64, kw, 160, fill=BOX, rx=10, sw=1.2)
        add('<circle cx="%g" cy="%g" r="26" fill="%s" stroke="%s" stroke-width="1.4"/>' % (kx+kw/2, y+112, IMG, STROKE))
        txt(kx+kw/2, y+117, "Foto", 11, "normal", MUT, "middle")
        bar(kx+kw/2-45, y+152, 90, 10)
        txt(kx+kw/2, y+192, "00 atendimentos", 12, "bold", MUT, "middle")
        txt(kx+20, y+92, "%d\u00ba" % (i+1), 16, "bold", MUT)
    y += 260
    nota(x, y+26, "Dados de exemplo em arquivo JavaScript; back-end ser\u00e1 ligado na pr\u00f3xima etapa")
    salvar("06-relatorios.svg")


if __name__ == "__main__":
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    login(); agenda(); agendamento(); barbearia(); historico(); relatorios()
