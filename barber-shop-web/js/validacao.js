/* ===========================================================================
   validacao.js - validação de formulários, sem recarregar a página
   Nenhuma mensagem usa alert(): o erro aparece ao lado do próprio campo,
   num elemento com aria-live para ser anunciado por leitores de tela.
   =========================================================================== */

const Validacao = {
    /** Marca o campo como inválido e mostra a mensagem abaixo dele. */
    marcarErro(entrada, mensagem) {
        const campo = entrada.closest('.campo');
        if (campo) campo.classList.add('campo--invalido');
        entrada.setAttribute('aria-invalid', 'true');

        const erro = document.getElementById(entrada.id + '-erro');
        if (erro) erro.textContent = mensagem;
    },

    /** Limpa o estado de erro de um campo. */
    limparErro(entrada) {
        const campo = entrada.closest('.campo');
        if (campo) campo.classList.remove('campo--invalido');
        entrada.removeAttribute('aria-invalid');

        const erro = document.getElementById(entrada.id + '-erro');
        if (erro) erro.textContent = '';
    },

    /** Limpa todos os erros de um formulário. */
    limparTodos(formulario) {
        formulario.querySelectorAll('[aria-invalid="true"]').forEach((entrada) => {
            Validacao.limparErro(entrada);
        });
    },

    /** Campo de preenchimento obrigatório. */
    obrigatorio(entrada, mensagem) {
        if (!entrada.value.trim()) {
            Validacao.marcarErro(entrada, mensagem || 'Preencha este campo.');
            return false;
        }
        return true;
    },

    /**
     * Contato aceito: telefone brasileiro com DDD, com ou sem máscara.
     * Ex.: (51) 99999-1234, 51999991234, 51 9999-1234.
     */
    telefone(entrada, mensagem) {
        const digitos = entrada.value.replace(/\D/g, '');
        if (digitos.length < 10 || digitos.length > 11) {
            Validacao.marcarErro(entrada, mensagem || 'Informe um telefone com DDD, ex.: (51) 99999-1234.');
            return false;
        }
        return true;
    },

    /** A data informada não pode ser anterior a hoje. */
    dataNaoPassada(entrada, mensagem) {
        const valor = entrada.value;
        if (!valor) return true;

        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);
        const escolhida = new Date(valor + 'T00:00:00');

        if (escolhida < hoje) {
            Validacao.marcarErro(entrada, mensagem || 'A data não pode ser no passado.');
            return false;
        }
        return true;
    },

    /** Escreve uma mensagem no painel de aviso do formulário. */
    avisar(seletor, mensagem, tipo) {
        const painel = document.querySelector(seletor);
        if (!painel) return;

        if (!mensagem) {
            painel.hidden = true;
            painel.textContent = '';
            return;
        }

        painel.hidden = false;
        painel.className = 'aviso aviso--' + (tipo || 'erro');
        painel.textContent = mensagem;
    },

    /** Limpa o erro assim que o usuário começa a corrigir o campo. */
    limparAoDigitar(formulario) {
        formulario.addEventListener('input', (evento) => {
            const alvo = evento.target;
            if (alvo.matches('input, select, textarea') && alvo.getAttribute('aria-invalid')) {
                Validacao.limparErro(alvo);
            }
        });
    }
};
